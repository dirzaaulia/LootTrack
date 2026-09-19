import os
import sys
import json
import time
import argparse
import requests
import jwt

if hasattr(sys.stdout, 'reconfigure'):
    sys.stdout.reconfigure(encoding='utf-8')

PACKAGE_NAME = "com.dirzaaulia.loottrack"
KEY_FILE = "fastlane/play-console-key.json"

APP_NAME_EN = "LootTrack - PC Game Deals"
APP_NAME_ID = "LootTrack - Diskon Game PC"

SHORT_DESC_EN = "PC game deals from 30+ stores. Price drop alerts & currency converter!"
SHORT_DESC_ID = "Diskon game PC dari 30+ toko. Notifikasi penurunan harga & konversi mata uang!"

FULL_DESC_EN = """🎮 LOOTTRACK — YOUR ULTIMATE PC GAME DEALS ENGINE

Never overpay for a PC game again. LootTrack scans deals across 30+ digital game stores in real time, compares prices, shows historical discounts, and lets you set custom price drop alerts — all displayed in your local currency.

━━━━━━━━━━━━━━━━━━━━━━━━━
🔥 CORE FEATURES
━━━━━━━━━━━━━━━━━━━━━━━━━

🔍 REAL-TIME DEAL DISCOVERY
Browse thousands of PC game deals sourced live from CheapShark, one of the most trusted game deal aggregators. Deals are sorted by Deal Rating, price range, Metacritic score, Steam rating, and more.

💰 MULTI-CURRENCY CONVERTER
View all prices in your local currency. LootTrack supports 9 currencies including USD, IDR, EUR, GBP, JPY, SGD, MYR, AUD, and CAD — powered by live exchange rates updated daily.

🔔 PRICE DROP ALERTS
Set a target price for any game and get notified by email the moment the price drops to or below your target. Never miss a flash sale on a game you've been watching.

📋 PERSONAL WATCHLIST
Save games you're tracking into your personal watchlist. View all your active alerts in one place and delete them when no longer needed.

📊 DEAL DETAILS & HISTORY
Tap any deal to view the game's full detail sheet — including current sale price, normal price, savings percentage, Deal Rating, Metacritic score, and Steam rating.

🏪 STORE PLATFORM FILTER
Filter deals by a specific digital storefront (Steam, Humble Store, Fanatical, GOG, Epic Games Store, and 25+ more) so you only see deals from stores you actually use.

🔃 ADVANCED SEARCH & SORT
Customize your deal feed with powerful filter options:
• Sort by Deal Rating, Price, Savings, or Title
• Set minimum Metacritic or Steam rating thresholds
• Filter to on-sale-only deals
• Search directly by game title

🌗 SLEEK CYBERPUNK THEME
Switch between a sleek dark mode and a clean light mode. Designed for ultimate legibility and late-night deal hunting.

━━━━━━━━━━━━━━━━━━━━━━━━━
📡 DATA SOURCES
━━━━━━━━━━━━━━━━━━━━━━━━━

• CheapShark REST API — Game deal lookup and cross-store price comparison across 30+ stores
• FawazAhmed Currency API — Real-time global exchange rate conversion

LootTrack is 100% free and supported by non-intrusive ads.

━━━━━━━━━━━━━━━━━━━━━━━━━
🔒 PRIVACY & PERMISSIONS
━━━━━━━━━━━━━━━━━━━━━━━━━

• Internet — To fetch live deals and exchange rates
• Notifications — To deliver price drop alerts (optional)

No account required to browse deals. An email address is only needed when setting an alert.

• Privacy Policy: https://lt.dirzaaulia.com/privacy
• Developer: Dirza Aulia (dirzaaulia.com)
• Support: dirzaaulia11@gmail.com
"""

FULL_DESC_ID = """🎮 LOOTTRACK — APLIKASI PELACAK DISKON GAME PC TERBAIK

Jangan pernah bayar kemahalan untuk game PC lagi. LootTrack memindai diskon di lebih dari 30 toko game digital secara real-time, membandingkan harga, menampilkan riwayat potongan harga, dan memungkinkan Anda memasang notifikasi penurunan harga — semuanya dalam mata uang lokal Anda.

━━━━━━━━━━━━━━━━━━━━━━━━━
🔥 FITUR UTAMA
━━━━━━━━━━━━━━━━━━━━━━━━━

🔍 TEMUKAN DISKON REAL-TIME
Jelajahi ribuan promo game PC yang diperbarui langsung dari CheapShark, agregator promo game terpercaya. Promo dapat diurutkan berdasarkan Deal Rating, rentang harga, skor Metacritic, rating Steam, dan lainnya.

💰 DUKUNGAN MATA UANG LOKAL
Lihat semua harga dalam mata uang Rupiah (IDR) atau mata uang internasional lainnya (USD, EUR, GBP, JPY, SGD, MYR, AUD, CAD) dengan kurs harian yang akurat.

🔔 NOTIFIKASI PENURUNAN HARGA
Pasang target harga untuk game incaran Anda dan dapatkan notifikasi email begitu harganya turun mencapai atau melebihi target. Jangan sampai ketinggalan flash sale!

📋 DAFTAR PANTAUAN PRIBADI
Simpan game yang sedang Anda pantau ke dalam watchlist pribadi. Kelola semua pengingat aktif Anda dalam satu tempat dengan mudah.

📊 DETAIL LENGKAP & RIWAYAT PROMO
Ketuk promo apa pun untuk melihat detail lengkap — termasuk harga diskon, harga normal, persentase hemat, Deal Rating, skor Metacritic, dan ulasan Steam.

🏪 FILTER PLATFORM TOKO
Saring promo berdasarkan toko favorit Anda (Steam, Humble Store, Fanatical, GOG, Epic Games Store, dan 25+ toko lainnya).

🔃 FILTER & PENCARIAN CANGGIH
Sesuaikan daftar promo dengan opsi pencarian:
• Urutkan berdasarkan Deal Rating, Harga, Penghematan, atau Judul
• Pasang batas minimum rating Steam atau Metacritic
• Filter hanya game yang sedang diskon
• Cari langsung berdasarkan judul game

🌗 TEMA DARK & LIGHT MODE
Gunakan tema gelap cyberpunk yang modern dan nyaman di mata untuk mencari promo di malam hari.

━━━━━━━━━━━━━━━━━━━━━━━━━
📡 SUMBER DATA
━━━━━━━━━━━━━━━━━━━━━━━━━

• CheapShark REST API — Agregator promo game lintas toko digital PC
• FawazAhmed Currency API — Konversi kurs mata uang global real-time

LootTrack 100% gratis dan didukung oleh iklan yang tidak mengganggu.

━━━━━━━━━━━━━━━━━━━━━━━━━
🔒 PRIVASI & IZIN
━━━━━━━━━━━━━━━━━━━━━━━━━

• Internet — Untuk mengambil data promo dan kurs mata uang
• Notifikasi — Untuk mengirim pengingat penurunan harga (opsional)

Tidak memerlukan akun untuk melihat diskon. Email hanya diperlukan saat memasang pengingat harga.

• Kebijakan Privasi: https://lt.dirzaaulia.com/privacy
• Pengembang: Dirza Aulia (dirzaaulia.com)
• Dukungan: dirzaaulia11@gmail.com
"""

LISTINGS = [
    {
        "lang": "en-US",
        "title": APP_NAME_EN,
        "short": SHORT_DESC_EN,
        "full": FULL_DESC_EN
    },
    {
        "lang": "id",
        "title": APP_NAME_ID,
        "short": SHORT_DESC_ID,
        "full": FULL_DESC_ID
    }
]

SCREENSHOT_FILES = [
    "screenshots/store_listing/01_deals_banner.png",
    "screenshots/store_listing/02_filters_banner.png",
    "screenshots/store_listing/03_currency_banner.png",
    "screenshots/store_listing/04_alerts_banner.png"
]

def get_access_token():
    with open(KEY_FILE, "r") as f:
        key_data = json.load(f)
        
    iat = int(time.time())
    payload = {
        "iss": key_data["client_email"],
        "sub": key_data["client_email"],
        "aud": "https://oauth2.googleapis.com/token",
        "iat": iat,
        "exp": iat + 3600,
        "scope": "https://www.googleapis.com/auth/androidpublisher"
    }
    encoded_jwt = jwt.encode(payload, key_data["private_key"], algorithm="RS256")
    resp = requests.post("https://oauth2.googleapis.com/token", data={
        "grant_type": "urn:ietf:params:oauth:grant-type:jwt-bearer",
        "assertion": encoded_jwt
    })
    return resp.json()["access_token"]

def main():
    parser = argparse.ArgumentParser(description="Update Google Play Store Listing for LootTrack.")
    parser.add_argument("--dry-run", action="store_true", help="Validate without committing")
    args = parser.parse_args()

    print("==================================================")
    print("  LootTrack Google Play Store Listing Updater")
    print(f"  Package: {PACKAGE_NAME}")
    print(f"  Languages: en-US, id")
    print(f"  Dry Run: {'YES' if args.dry_run else 'NO (LIVE UPDATE)'}")
    print("==================================================")

    token = get_access_token()
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }

    # 1. Create Edit Session
    print("\n[1/5] Creating edit session...")
    edit_resp = requests.post(
        f"https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{PACKAGE_NAME}/edits",
        headers=headers
    )
    if edit_resp.status_code != 200:
        print(f"  [FAIL] Could not create edit session: {edit_resp.status_code} {edit_resp.text}")
        return

    edit_id = edit_resp.json()["id"]
    print(f"  Active Edit ID: {edit_id}")

    try:
        # 2. Update Text Listings (Title, Short Desc, Full Desc)
        print("\n[2/5] Updating store listings for all languages...")
        for item in LISTINGS:
            lang = item["lang"]
            payload = {
                "language": lang,
                "title": item["title"],
                "shortDescription": item["short"],
                "fullDescription": item["full"]
            }
            res = requests.put(
                f"https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{PACKAGE_NAME}/edits/{edit_id}/listings/{lang}",
                headers=headers,
                json=payload
            )
            if res.status_code in (200, 201):
                print(f"  [OK] Listing '{lang}' text updated successfully.")
            else:
                print(f"  [FAIL] Listing '{lang}' text failed: {res.status_code} {res.text}")

        # 3. Upload App Icon (512x512)
        icon_path = "androidApp/src/main/ic_launcher-playstore.png"
        if os.path.exists(icon_path):
            print("\n[3/5] Uploading official 512x512 app icon...")
            for item in LISTINGS:
                lang = item["lang"]
                with open(icon_path, "rb") as icon_file:
                    icon_headers = {
                        "Authorization": f"Bearer {token}",
                        "Content-Type": "image/png"
                    }
                    icon_url = (
                        f"https://androidpublisher.googleapis.com/upload/androidpublisher/v3/applications/"
                        f"{PACKAGE_NAME}/edits/{edit_id}/listings/{lang}/icon"
                    )
                    icon_res = requests.post(icon_url, headers=icon_headers, data=icon_file)
                    if icon_res.status_code in (200, 201):
                        print(f"  [OK] Uploaded app icon for '{lang}'.")
                    else:
                        print(f"  [FAIL] Failed to upload app icon for '{lang}': {icon_res.status_code} {icon_res.text}")

        # 4. Upload Feature Graphic (1024x500)
        feature_graphic_path = "screenshots/store_listing/feature_graphic.png"
        if os.path.exists(feature_graphic_path):
            print("\n[4/5] Uploading 1024x500 feature graphic...")
            for item in LISTINGS:
                lang = item["lang"]
                # Delete existing feature graphic if any
                requests.delete(
                    f"https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{PACKAGE_NAME}/edits/{edit_id}/listings/{lang}/featureGraphic",
                    headers=headers
                )
                with open(feature_graphic_path, "rb") as feat_file:
                    feat_headers = {
                        "Authorization": f"Bearer {token}",
                        "Content-Type": "image/png"
                    }
                    feat_url = (
                        f"https://androidpublisher.googleapis.com/upload/androidpublisher/v3/applications/"
                        f"{PACKAGE_NAME}/edits/{edit_id}/listings/{lang}/featureGraphic"
                    )
                    feat_res = requests.post(feat_url, headers=feat_headers, data=feat_file)
                    if feat_res.status_code in (200, 201):
                        print(f"  [OK] Uploaded feature graphic for '{lang}'.")
                    else:
                        print(f"  [FAIL] Failed to upload feature graphic for '{lang}': {feat_res.status_code} {feat_res.text}")

        # 5. Upload Promotional Phone Screenshots (1080x2400)
        print("\n[5/5] Uploading promotional phone screenshots...")
        for item in LISTINGS:
            lang = item["lang"]
            # Clear old screenshots
            requests.delete(
                f"https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{PACKAGE_NAME}/edits/{edit_id}/listings/{lang}/phoneScreenshots",
                headers=headers
            )
            uploaded_count = 0
            for idx, img_path in enumerate(SCREENSHOT_FILES, 1):
                if not os.path.exists(img_path):
                    continue
                with open(img_path, "rb") as img_file:
                    up_headers = {
                        "Authorization": f"Bearer {token}",
                        "Content-Type": "image/png"
                    }
                    img_url = (
                        f"https://androidpublisher.googleapis.com/upload/androidpublisher/v3/applications/"
                        f"{PACKAGE_NAME}/edits/{edit_id}/listings/{lang}/phoneScreenshots"
                    )
                    up_res = requests.post(img_url, headers=up_headers, data=img_file)
                    if up_res.status_code in (200, 201):
                        uploaded_count += 1
                    else:
                        print(f"  [FAIL] Upload failed for {img_path}: {up_res.status_code} {up_res.text}")
            print(f"  [OK] Uploaded {uploaded_count} phone screenshots for '{lang}'.")

        # Validation or Commit
        if args.dry_run:
            print("\nValidating edit session (Dry Run)...")
            val_res = requests.post(
                f"https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{PACKAGE_NAME}/edits/{edit_id}:validate",
                headers=headers
            )
            if val_res.status_code == 200:
                print("  [SUCCESS] All listings, graphics, and screenshots validated successfully by Google Play!")
            else:
                print(f"  [FAIL] Validation returned: {val_res.status_code} {val_res.text}")
            requests.delete(
                f"https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{PACKAGE_NAME}/edits/{edit_id}",
                headers=headers
            )
            print("  Cleaned up edit session.")
        else:
            print("\nCommitting changes LIVE to Google Play Console...")
            commit_res = requests.post(
                f"https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{PACKAGE_NAME}/edits/{edit_id}:commit",
                headers=headers
            )
            if commit_res.status_code == 200:
                print("  [SUCCESS] All store listings, icon, feature graphic, and screenshots committed LIVE!")
            else:
                print(f"  [FAIL] Commit failed: {commit_res.status_code} {commit_res.text}")

    except Exception as e:
        print("Exception occurred:", e)
        try:
            requests.delete(
                f"https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{PACKAGE_NAME}/edits/{edit_id}",
                headers=headers
            )
        except:
            pass

if __name__ == "__main__":
    main()
