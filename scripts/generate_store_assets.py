import os
from PIL import Image, ImageDraw, ImageFont, ImageFilter

def create_rounded_mask(size, radius):
    mask = Image.new('L', size, 0)
    draw = ImageDraw.Draw(mask)
    draw.rounded_rectangle([(0, 0), size], radius=radius, fill=255)
    return mask

def generate_banner(
    raw_screenshot_path: str,
    output_path: str,
    badge_text: str,
    headline_text: str,
    subtext: str,
    badge_color: tuple = (255, 42, 109), # Cyberpunk hot pink
):
    CANVAS_WIDTH = 1080
    CANVAS_HEIGHT = 2400
    
    # 1. Base Canvas with smooth dark cyberpunk gradient
    base = Image.new('RGBA', (CANVAS_WIDTH, CANVAS_HEIGHT), (13, 10, 24, 255))
    draw = ImageDraw.Draw(base)
    
    for y in range(CANVAS_HEIGHT):
        ratio = y / CANVAS_HEIGHT
        r = int(18 * (1 - ratio) + 8 * ratio)
        g = int(14 * (1 - ratio) + 10 * ratio)
        b = int(32 * (1 - ratio) + 20 * ratio)
        draw.line([(0, y), (CANVAS_WIDTH, y)], fill=(r, g, b, 255))
    
    # 2. Ambient top glow
    glow = Image.new('RGBA', (CANVAS_WIDTH, CANVAS_HEIGHT), (0, 0, 0, 0))
    glow_draw = ImageDraw.Draw(glow)
    glow_draw.ellipse(
        [(CANVAS_WIDTH // 2 - 380, -80), (CANVAS_WIDTH // 2 + 380, 520)],
        fill=(badge_color[0], badge_color[1], badge_color[2], 35)
    )
    glow = glow.filter(ImageFilter.GaussianBlur(100))
    base = Image.alpha_composite(base, glow)
    
    # 3. Typography setup
    font_bold = "C:/Windows/Fonts/segoeuib.ttf"
    font_regular = "C:/Windows/Fonts/segoeui.ttf"
    
    badge_font = ImageFont.truetype(font_bold, 26)
    title_font = ImageFont.truetype(font_bold, 58)
    sub_font = ImageFont.truetype(font_regular, 30)
    
    # Measure badge
    badge_bbox = badge_font.getbbox(badge_text)
    badge_w = badge_bbox[2] - badge_bbox[0]
    badge_h = badge_bbox[3] - badge_bbox[1]
    
    pad_x = 28
    pad_y = 12
    box_w = badge_w + pad_x * 2
    box_h = badge_h + pad_y * 2
    badge_x = (CANVAS_WIDTH - box_w) // 2
    badge_y = 110
    
    # Draw badge pill on separate RGBA layer for perfect alpha blending
    badge_layer = Image.new('RGBA', (CANVAS_WIDTH, CANVAS_HEIGHT), (0, 0, 0, 0))
    badge_draw = ImageDraw.Draw(badge_layer)
    badge_draw.rounded_rectangle(
        [(badge_x, badge_y), (badge_x + box_w, badge_y + box_h)],
        radius=box_h // 2,
        fill=(badge_color[0], badge_color[1], badge_color[2], 45),
        outline=(badge_color[0], badge_color[1], badge_color[2], 200),
        width=2
    )
    badge_draw.text(
        (badge_x + pad_x, badge_y + pad_y - badge_bbox[1] - 1),
        badge_text,
        font=badge_font,
        fill=(badge_color[0], badge_color[1], badge_color[2], 255)
    )
    base = Image.alpha_composite(base, badge_layer)
    
    # 4. Draw Headline & Subtext
    text_layer = Image.new('RGBA', (CANVAS_WIDTH, CANVAS_HEIGHT), (0, 0, 0, 0))
    text_draw = ImageDraw.Draw(text_layer)
    
    title_bbox = title_font.getbbox(headline_text)
    title_w = title_bbox[2] - title_bbox[0]
    title_x = (CANVAS_WIDTH - title_w) // 2
    title_y = badge_y + box_h + 28
    text_draw.text((title_x, title_y), headline_text, font=title_font, fill=(255, 255, 255, 255))
    
    sub_bbox = sub_font.getbbox(subtext)
    sub_w = sub_bbox[2] - sub_bbox[0]
    sub_x = (CANVAS_WIDTH - sub_w) // 2
    sub_y = title_y + 80
    text_draw.text((sub_x, sub_y), subtext, font=sub_font, fill=(160, 168, 185, 255))
    base = Image.alpha_composite(base, text_layer)
    
    # 5. Process Phone Screenshot
    phone_ui = Image.open(raw_screenshot_path).convert('RGBA')
    
    TARGET_UI_WIDTH = 930
    aspect = phone_ui.height / phone_ui.width
    TARGET_UI_HEIGHT = int(TARGET_UI_WIDTH * aspect)
    
    phone_ui_resized = phone_ui.resize((TARGET_UI_WIDTH, TARGET_UI_HEIGHT), Image.Resampling.LANCZOS)
    
    CORNER_RADIUS = 52
    mask = create_rounded_mask((TARGET_UI_WIDTH, TARGET_UI_HEIGHT), CORNER_RADIUS)
    
    ui_x = (CANVAS_WIDTH - TARGET_UI_WIDTH) // 2
    ui_y = 520
    
    # Drop shadow
    shadow = Image.new('RGBA', (CANVAS_WIDTH, CANVAS_HEIGHT), (0, 0, 0, 0))
    shadow_draw = ImageDraw.Draw(shadow)
    shadow_draw.rounded_rectangle(
        [(ui_x - 6, ui_y + 16), (ui_x + TARGET_UI_WIDTH + 6, ui_y + TARGET_UI_HEIGHT + 24)],
        radius=CORNER_RADIUS + 6,
        fill=(0, 0, 0, 220)
    )
    shadow = shadow.filter(ImageFilter.GaussianBlur(36))
    base = Image.alpha_composite(base, shadow)
    
    # Paste screenshot
    base.paste(phone_ui_resized, (ui_x, ui_y), mask)
    
    # Border stroke
    stroke_layer = Image.new('RGBA', (CANVAS_WIDTH, CANVAS_HEIGHT), (0, 0, 0, 0))
    stroke_draw = ImageDraw.Draw(stroke_layer)
    stroke_draw.rounded_rectangle(
        [(ui_x, ui_y), (ui_x + TARGET_UI_WIDTH, ui_y + TARGET_UI_HEIGHT)],
        radius=CORNER_RADIUS,
        outline=(255, 255, 255, 45),
        width=3
    )
    base = Image.alpha_composite(base, stroke_layer)
    
    # 6. Save final RGB PNG
    final_img = base.convert('RGB')
    final_img.save(output_path, 'PNG', quality=95)
    print(f"Generated: {output_path}")

def generate_feature_graphic(output_path: str, icon_path: str):
    WIDTH = 1024
    HEIGHT = 500
    
    base = Image.new('RGBA', (WIDTH, HEIGHT), (12, 10, 24, 255))
    draw = ImageDraw.Draw(base)
    
    for y in range(HEIGHT):
        ratio = y / HEIGHT
        r = int(22 * (1 - ratio) + 8 * ratio)
        g = int(12 * (1 - ratio) + 8 * ratio)
        b = int(36 * (1 - ratio) + 20 * ratio)
        draw.line([(0, y), (WIDTH, y)], fill=(r, g, b, 255))
    
    # Dual Neon Ambient Glow (Pink on left, Cyan on right)
    glow = Image.new('RGBA', (WIDTH, HEIGHT), (0, 0, 0, 0))
    glow_draw = ImageDraw.Draw(glow)
    # Left Pink glow
    glow_draw.ellipse([(-100, -50), (450, 550)], fill=(255, 42, 109, 45))
    # Right Cyan glow
    glow_draw.ellipse([(600, -50), (1150, 550)], fill=(5, 217, 232, 35))
    glow = glow.filter(ImageFilter.GaussianBlur(90))
    base = Image.alpha_composite(base, glow)
    
    # Load and render app icon
    if os.path.exists(icon_path):
        icon = Image.open(icon_path).convert('RGBA')
        ICON_SIZE = 220
        icon_resized = icon.resize((ICON_SIZE, ICON_SIZE), Image.Resampling.LANCZOS)
        icon_mask = create_rounded_mask((ICON_SIZE, ICON_SIZE), 48)
        
        icon_x = 90
        icon_y = (HEIGHT - ICON_SIZE) // 2
        
        # Icon shadow
        icon_shadow = Image.new('RGBA', (WIDTH, HEIGHT), (0, 0, 0, 0))
        is_draw = ImageDraw.Draw(icon_shadow)
        is_draw.rounded_rectangle([(icon_x - 4, icon_y + 12), (icon_x + ICON_SIZE + 4, icon_y + ICON_SIZE + 18)], radius=52, fill=(0, 0, 0, 200))
        icon_shadow = icon_shadow.filter(ImageFilter.GaussianBlur(24))
        base = Image.alpha_composite(base, icon_shadow)
        
        # Paste Icon
        base.paste(icon_resized, (icon_x, icon_y), icon_mask)
        
        # Border
        b_layer = Image.new('RGBA', (WIDTH, HEIGHT), (0, 0, 0, 0))
        b_draw = ImageDraw.Draw(b_layer)
        b_draw.rounded_rectangle([(icon_x, icon_y), (icon_x + ICON_SIZE, icon_y + ICON_SIZE)], radius=48, outline=(255, 255, 255, 60), width=3)
        base = Image.alpha_composite(base, b_layer)
    
    # Typography
    font_bold = "C:/Windows/Fonts/segoeuib.ttf"
    font_regular = "C:/Windows/Fonts/segoeui.ttf"
    
    app_title_font = ImageFont.truetype(font_bold, 64)
    tagline_font = ImageFont.truetype(font_bold, 30)
    features_font = ImageFont.truetype(font_regular, 22)
    
    text_layer = Image.new('RGBA', (WIDTH, HEIGHT), (0, 0, 0, 0))
    t_draw = ImageDraw.Draw(text_layer)
    
    text_x = 350
    t_draw.text((text_x, 115), "LOOTTRACK", font=app_title_font, fill=(255, 255, 255, 255))
    t_draw.text((text_x, 195), "Never Overpay for PC Games Again", font=tagline_font, fill=(255, 42, 109, 255))
    
    # Feature pill badges
    pills = ["30+ PC Stores", "Real-Time Deals", "Price Drop Alerts", "Multi-Currency"]
    pill_font = ImageFont.truetype(font_bold, 18)
    pill_x = text_x
    pill_y = 265
    
    for pill in pills:
        bbox = pill_font.getbbox(pill)
        pw = bbox[2] - bbox[0] + 24
        ph = bbox[3] - bbox[1] + 16
        
        t_draw.rounded_rectangle([(pill_x, pill_y), (pill_x + pw, pill_y + ph)], radius=ph // 2, fill=(255, 255, 255, 18), outline=(5, 217, 232, 180), width=1)
        t_draw.text((pill_x + 12, pill_y + 8 - bbox[1]), pill, font=pill_font, fill=(5, 217, 232, 255))
        pill_x += pw + 12
        if pill_x > WIDTH - 160:
            pill_x = text_x
            pill_y += ph + 10
            
    base = Image.alpha_composite(base, text_layer)
    
    final_img = base.convert('RGB')
    final_img.save(output_path, 'PNG', quality=95)
    print(f"Generated Feature Graphic: {output_path}")

def main():
    os.makedirs('screenshots/store_listing', exist_ok=True)
    
    icon_path = "androidApp/src/main/ic_launcher-playstore.png"
    feature_graphic_path = "screenshots/store_listing/feature_graphic.png"
    generate_feature_graphic(feature_graphic_path, icon_path)
    
    slides = [
        {
            "raw": "screenshots/raw/01_deals.png",
            "out": "screenshots/store_listing/01_deals_banner.png",
            "badge": "LIVE PC GAME DEALS",
            "title": "Track 30+ PC Game Stores",
            "sub": "Real-time discounts, flash deals & historical lows",
            "color": (255, 42, 109) # Hot Pink
        },
        {
            "raw": "screenshots/raw/02_filter.png",
            "out": "screenshots/store_listing/02_filters_banner.png",
            "badge": "POWERFUL FILTERS",
            "title": "Filter What You Want",
            "sub": "Storefronts, price range, Steam & Metacritic rating",
            "color": (5, 217, 232) # Cyan
        },
        {
            "raw": "screenshots/raw/03_currency.png",
            "out": "screenshots/store_listing/03_currency_banner.png",
            "badge": "GLOBAL CONVERSION",
            "title": "Multi-Currency Support",
            "sub": "Live rates for USD, IDR, EUR, GBP, JPY & more",
            "color": (168, 85, 247) # Purple
        },
        {
            "raw": "screenshots/raw/04_saved_alerts.png",
            "out": "screenshots/store_listing/04_alerts_banner.png",
            "badge": "24/7 MONITORING",
            "title": "Instant Price Drop Alerts",
            "sub": "Set target price and get notified when it drops",
            "color": (245, 158, 11) # Amber
        }
    ]
    
    for s in slides:
        generate_banner(
            raw_screenshot_path=s["raw"],
            output_path=s["out"],
            badge_text=s["badge"],
            headline_text=s["title"],
            subtext=s["sub"],
            badge_color=s["color"]
        )

if __name__ == "__main__":
    main()
