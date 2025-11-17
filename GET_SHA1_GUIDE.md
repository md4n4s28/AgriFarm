# 🔑 Get SHA-1 Certificate Fingerprint

## Method 1: Using Gradle (Easiest)

### In Android Studio:

1. Open **Gradle** panel (right side)
2. Navigate to: **AgriFarmV001 → app → Tasks → android → signingReport**
3. Double-click **signingReport**
4. Wait for task to complete
5. Check **Run** tab at bottom
6. Look for output like:

```
Variant: debug
Config: debug
Store: C:\Users\YOUR_NAME\.android\debug.keystore
Alias: AndroidDebugKey
MD5: XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX
SHA1: AA:BB:CC:DD:EE:FF:11:22:33:44:55:66:77:88:99:00:AA:BB:CC:DD
SHA-256: ...
```

**Copy the SHA1 value** (the one with colons)

---

## Method 2: Using Terminal in Android Studio

### Steps:

1. Open **Terminal** in Android Studio (bottom)
2. Run this command:

**For Windows:**
```bash
gradlew signingReport
```

**For Mac/Linux:**
```bash
./gradlew signingReport
```

3. Look for the SHA1 in output

---

## Method 3: Using Command Line (Manual)

### For Debug Keystore:

**Windows:**
```bash
cd C:\Users\YOUR_USERNAME\.android
keytool -list -v -keystore debug.keystore -alias androiddebugkey -storepass android -keypass android
```

**Mac/Linux:**
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

### Output will show:
```
Certificate fingerprints:
    SHA1: AA:BB:CC:DD:EE:FF:11:22:33:44:55:66:77:88:99:00:AA:BB:CC:DD
    SHA256: ...
```

---

## Method 4: If keytool not found

### Add Java to PATH:

**Windows:**
1. Find Java installation: `C:\Program Files\Android\Android Studio\jbr\bin`
2. Add to PATH or use full path:
```bash
"C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe" -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
```

**Mac:**
```bash
/Applications/Android\ Studio.app/Contents/jbr/Contents/Home/bin/keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

---

## Quick Copy-Paste Commands

### Windows (PowerShell):
```powershell
cd $env:USERPROFILE\.android
& "C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe" -list -v -keystore debug.keystore -alias androiddebugkey -storepass android -keypass android | Select-String "SHA1"
```

### Windows (CMD):
```cmd
cd %USERPROFILE%\.android
"C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe" -list -v -keystore debug.keystore -alias androiddebugkey -storepass android -keypass android | findstr "SHA1"
```

---

## What to Do with SHA-1

### In Google Cloud Console:

1. Go to **APIs & Services → Credentials**
2. Click **+ CREATE CREDENTIALS → OAuth client ID**
3. Select **Android**
4. Enter:
   - **Package name:** `com.agrifarm.app`
   - **SHA-1 certificate fingerprint:** `AA:BB:CC:DD:...` (paste here)
5. Click **CREATE**
6. Copy the **Client ID**

---

## Troubleshooting

### "keytool is not recognized"
- Use Method 1 (Gradle) - easiest!
- Or use full path to keytool (see Method 4)

### "debug.keystore not found"
- Build the app once first
- Android Studio creates it automatically
- Location: `C:\Users\YOUR_NAME\.android\debug.keystore`

### Multiple SHA-1 values shown
- Use the one under **Variant: debug**
- For production, use **Variant: release**

### Need release SHA-1 (for production)
```bash
keytool -list -v -keystore YOUR_RELEASE_KEYSTORE.jks -alias YOUR_ALIAS
```
(You'll need your keystore password)

---

## ✅ Recommended: Use Method 1 (Gradle)

**Easiest and most reliable!**

1. Gradle panel → signingReport
2. Copy SHA1
3. Paste in Google Cloud Console
4. Done! 🎉

---

**Next Steps:**
1. Get SHA-1 using any method above
2. Create OAuth client ID in Google Cloud Console
3. Continue with `GOOGLE_AUTH_SETUP.md`
