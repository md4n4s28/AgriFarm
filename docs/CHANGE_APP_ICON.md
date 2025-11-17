# 🎨 How to Change App Icon

## Method 1: Using Android Studio (Easiest)

### Step 1: Prepare Your Logo
- Create a logo image (PNG format)
- Recommended size: 512x512 pixels
- Use green color (#4CAF50) for brand consistency
- Transparent background works best

### Step 2: Use Image Asset Studio
1. In Android Studio, right-click on `app` folder
2. Select **New → Image Asset**
3. Choose **Launcher Icons (Adaptive and Legacy)**
4. In "Foreground Layer":
   - Select "Image" as Source Asset
   - Click folder icon and choose your logo
   - Adjust padding/scaling as needed
5. Click **Next** → **Finish**

### Step 3: Run App
- The new icon will appear automatically
- Test on device/emulator

---

## Method 2: Manual Replacement

### Replace these files in `app/src/main/res/`:

```
mipmap-mdpi/ic_launcher.png (48x48)
mipmap-hdpi/ic_launcher.png (72x72)
mipmap-xhdpi/ic_launcher.png (96x96)
mipmap-xxhdpi/ic_launcher.png (144x144)
mipmap-xxxhdpi/ic_launcher.png (192x192)
```

---

## Quick Icon Ideas for AgriFarm:

### Option 1: Leaf Icon
- Simple green leaf
- Represents agriculture
- Clean and modern

### Option 2: Farm Icon
- Barn or field silhouette
- Green color scheme
- Professional look

### Option 3: Plant Sprout
- Growing plant
- Symbolizes growth
- Minimalist design

### Option 4: Letter "A"
- Stylized "A" with leaf
- Brand identity
- Easy to recognize

---

## Free Icon Resources:

1. **Flaticon** - flaticon.com
2. **Icons8** - icons8.com
3. **Freepik** - freepik.com
4. **Canva** - canva.com (create custom)

---

## Recommended Colors:

- **Primary Green:** #4CAF50
- **Dark Green:** #2E7D32
- **White:** #FFFFFF
- **Black:** #000000

---

## Testing:

After changing icon:
1. Uninstall old app
2. Clean project: Build → Clean Project
3. Rebuild: Build → Rebuild Project
4. Install fresh: Run ▶️

---

**Current Icon Location:**
`app/src/main/res/mipmap-*/ic_launcher.png`
