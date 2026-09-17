import { Resend } from 'resend';

export interface Env {
	DB?: any;
	RESEND_API_KEY: string;
	SENDER_EMAIL?: string;
}

export default {
	// 1. Scheduled Handler (Runs 24/7 Hourly Cron)
	async scheduled(event: ScheduledEvent, env: Env, ctx: ExecutionContext): Promise<void> {
		console.log(`[LootTrack Cron] Running hourly price alert check at ${new Date().toISOString()}`);
		const resend = new Resend(env.RESEND_API_KEY);
		const senderEmail = env.SENDER_EMAIL || 'LootTrack Alerts <onboarding@resend.dev>';

		try {
			if (!env.DB) {
				console.log('[LootTrack Cron] D1 DB binding not connected yet.');
				return;
			}

			// Query active user alerts
			const { results } = await env.DB.prepare(
				"SELECT * FROM active_user_alerts WHERE status = 'ACTIVE'"
			).all();

			if (!results || results.length === 0) {
				console.log('[LootTrack Cron] No active price alerts found.');
				return;
			}

			for (const alert of results) {
				const gameId = alert.game_id;
				const userEmail = alert.user_email;
				const targetPriceUsd = parseFloat(alert.target_price_usd);

				// Fetch current live deals from CheapShark API
				const response = await fetch(`https://www.cheapshark.com/api/1.0/games?id=${gameId}`);
				if (!response.ok) continue;

				const gameData: any = await response.json();
				const deals = gameData?.deals || [];
				if (deals.length === 0) continue;

				// Find lowest active price across all stores
				const cheapestDeal = deals.reduce((min: any, deal: any) =>
					parseFloat(deal.price) < parseFloat(min.price) ? deal : min, deals[0]
				);

				const currentPriceUsd = parseFloat(cheapestDeal.price);

				// Check if current price meets or beats user's target price
				if (currentPriceUsd <= targetPriceUsd) {
					console.log(`[LootTrack Alert Triggered] Game ${gameId} dropped to $${currentPriceUsd} for ${userEmail}`);

					// Send Custom LootTrack HTML Email via Resend
					await resend.emails.send({
						from: senderEmail,
						to: userEmail,
						subject: `🔥 Price Drop Alert: ${gameData.info?.title || 'Game'} is now $${currentPriceUsd}!`,
						html: `
							<div style="font-family: Arial, sans-serif; background-color: #090710; color: #ffffff; padding: 24.dp; border: 1px solid #FF1E6B;">
								<h1 style="color: #FF1E6B; margin-bottom: 4px;">LOOTTRACK PRICE DROP ALERT</h1>
								<p style="color: #8B5CF6; font-size: 12px; font-weight: bold; letter-spacing: 1px; margin-top: 0;">24/7 AUTOMATIC NOTIFICATION</p>
								<hr style="border-color: #333;" />
								<h2 style="color: #ffffff;">${gameData.info?.title || 'Tracked Game'}</h2>
								<p style="font-size: 16px;">Current Price: <span style="color: #FF1E6B; font-weight: bold;">$${currentPriceUsd}</span> (Target: $${targetPriceUsd})</p>
								<div style="margin-top: 20px;">
									<a href="https://www.cheapshark.com/redirect?dealID=${cheapestDeal.dealID}" style="background-color: #FF1E6B; color: #ffffff; padding: 12px 24px; text-decoration: none; font-weight: bold; display: inline-block;">CLAIM DEAL ON LOOTTRACK</a>
								</div>
							</div>
						`
					});

					// Mark alert as triggered
					await env.DB.prepare(
						"UPDATE active_user_alerts SET status = 'TRIGGERED' WHERE alert_id = ?"
					).bind(alert.alert_id).run();
				}
			}
		} catch (error) {
			console.error('[LootTrack Cron Error]', error);
		}
	},

	// 2. Fetch Handler (API Router for Alert Registration)
	async fetch(request: Request, env: Env, ctx: ExecutionContext): Promise<Response> {
		const url = new URL(request.url);

		if (request.method === 'OPTIONS') {
			return new Response(null, {
				headers: {
					'Access-Control-Allow-Origin': '*',
					'Access-Control-Allow-Methods': 'GET, POST, OPTIONS',
					'Access-Control-Allow-Headers': 'Content-Type'
				}
			});
		}

		if (url.pathname === '/api/alerts/register' && request.method === 'POST') {
			try {
				const body: any = await request.json();
				const { gameId, userEmail, targetPriceUsd } = body;

				if (!gameId || !userEmail || !targetPriceUsd) {
					return new Response(JSON.stringify({ success: false, message: 'Missing parameters' }), { status: 400 });
				}

				if (env.DB) {
					const alertId = `${gameId}_${userEmail}_${Date.now()}`;
					await env.DB.prepare(
						"INSERT OR REPLACE INTO active_user_alerts (alert_id, game_id, user_email, target_price_usd, status) VALUES (?, ?, ?, ?, 'ACTIVE')"
					).bind(alertId, gameId, userEmail, targetPriceUsd.toString()).run();
				}

				return new Response(JSON.stringify({ success: true, message: 'Alert registered successfully' }), {
					headers: { 'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*' }
				});
			} catch (e: any) {
				return new Response(JSON.stringify({ success: false, error: e.message }), { status: 500 });
			}
		}

		return new Response(JSON.stringify({ service: 'LootTrack Cloudflare Worker API', status: 'ONLINE' }), {
			headers: { 'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*' }
		});
	}
};
