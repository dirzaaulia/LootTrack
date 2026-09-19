import os
import sys
import time
import subprocess

if hasattr(sys.stdout, 'reconfigure'):
    sys.stdout.reconfigure(encoding='utf-8')

DEVICE_ID = "ZP22234TQL"
PACKAGE_NAME = "com.dirzaaulia.loottrack"
ACTIVITY_NAME = f"{PACKAGE_NAME}/.MainActivity"

def run_adb(cmd, check=True):
    full_cmd = f"adb -s {DEVICE_ID} {cmd}"
    result = subprocess.run(full_cmd, shell=True, capture_output=True, text=True)
    if check and result.returncode != 0:
        print(f"ADB Error on '{cmd}': {result.stderr.strip()}")
    return result

def enable_demo_mode():
    print("[1/5] Enabling Android SystemUI Demo Mode...")
    run_adb("shell settings put global sysui_demo_allowed 1")
    run_adb("shell am broadcast -a com.android.systemui.demo -e command enter")
    run_adb("shell am broadcast -a com.android.systemui.demo -e command clock -e hhmm 1200")
    run_adb("shell am broadcast -a com.android.systemui.demo -e command battery -e plugged false -e level 100")
    run_adb("shell am broadcast -a com.android.systemui.demo -e command network -e wifi show -e level 4")
    run_adb("shell am broadcast -a com.android.systemui.demo -e command network -e mobile show -e datatype lte -e level 4")
    run_adb("shell am broadcast -a com.android.systemui.demo -e command notifications -e visible false")

def exit_demo_mode():
    print("Exiting Android SystemUI Demo Mode...")
    run_adb("shell am broadcast -a com.android.systemui.demo -e command exit")

def capture_screen(output_path):
    with open(output_path, 'wb') as f:
        subprocess.run(['adb', '-s', DEVICE_ID, 'exec-out', 'screencap', '-p'], stdout=f)
    print(f"  [OK] Saved screenshot: {output_path}")

def main():
    os.makedirs('screenshots/raw', exist_ok=True)
    
    try:
        enable_demo_mode()
        
        print("\n[2/5] Launching LootTrack in clean demo mode (hide_ads=true)...")
        run_adb(f"shell am start -S -n {ACTIVITY_NAME} --ez hide_ads true --ez demo_mode true")
        time.sleep(7.0) # Wait for splash screen & deal data network fetch to complete
        
        # 1. Capture Main Deals Feed
        print("\n[3/5] Capturing screen 1: Deals & Featured Drop...")
        capture_screen("screenshots/raw/01_deals.png")
        time.sleep(1)
        
        # 2. Open & Capture Filters Sheet
        print("Capturing screen 2: Advanced Store & Rating Filters...")
        run_adb("shell input tap 1100 430") # Tap FILTER button
        time.sleep(1.5)
        capture_screen("screenshots/raw/02_filter.png")
        run_adb("shell input tap 1140 390") # Tap CLOSE button on filter sheet
        time.sleep(1)
        
        # 3. Open & Capture Currency Selector
        print("Capturing screen 3: Multi-Currency Converter...")
        run_adb("shell input tap 980 240") # Tap EUR/Currency button in top bar
        time.sleep(1.5)
        capture_screen("screenshots/raw/03_currency.png")
        run_adb("shell input tap 1140 1630") # Tap CLOSE button on currency sheet
        time.sleep(1)
        
        # 4. Open & Capture Saved Alerts Tab
        print("Capturing screen 4: Tracked Alerts & Watchlist...")
        run_adb("shell input tap 520 2620") # Tap SAVED bottom tab
        time.sleep(1.5)
        capture_screen("screenshots/raw/04_saved_alerts.png")
        time.sleep(0.5)
        
        # Return to Deals tab
        run_adb("shell input tap 320 2620")
        
        print("\n[4/5] All raw screenshots captured with clean status bar and no ads!")
        
    finally:
        exit_demo_mode()

if __name__ == "__main__":
    main()
