# Google Play App Quality Compliance Plan

This plan addresses the new Google Play quality requirements regarding **App Memory Usage** and **Secure Device Migration**.

## User Review Required

> [!IMPORTANT]
> **Data Backup Privacy:** Enabling `android:allowBackup="true"` will allow user history and settings to be backed up to Google Drive (Cloud) and transferred during device setup. If there is any sensitive data that should *never* leave the device, it must be explicitly excluded in the backup rules.

## Proposed Changes

### Secure Device Migration (Seamless Upgrade)
Enabling the Android Backup Service ensures that when a user upgrades their device, their tool logs and settings are automatically restored.

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/Rudra/AndroidStudioProjects/ISPNetworkToolv1/app/src/main/AndroidManifest.xml)
- Set `android:allowBackup="true"`.

#### [MODIFY] [backup_rules.xml](file:///C:/Users/Rudra/AndroidStudioProjects/ISPNetworkToolv1/app/src/main/res/xml/backup_rules.xml)
- Configure rules to include the Room database and shared preferences.

#### [MODIFY] [data_extraction_rules.xml](file:///C:/Users/Rudra/AndroidStudioProjects/ISPNetworkToolv1/app/src/main/res/xml/data_extraction_rules.xml)
- Specify cloud backup and device-to-device transfer rules for history data.

---

### App Memory Usage & Optimization
Optimizing memory usage to stay within the new Google Play performance thresholds (Anonymous RSS + Swap).

#### [MODIFY] [ISPNetworkToolApplication.kt](file:///C:/Users/Rudra/AndroidStudioProjects/ISPNetworkToolv1/app/src/main/java/com/rudra/ispnetworktool/ISPNetworkToolApplication.kt)
- Implement `onTrimMemory` to clear caches or notify components to release memory when the system is under pressure.

#### [MODIFY] [proguard-rules.pro](file:///C:/Users/Rudra/AndroidStudioProjects/ISPNetworkToolv1/app/proguard-rules.pro)
- Audit and ensure minimal keep rules to maximize R8 optimization coverage (targeting >25% as required).

---

### Documentation & Sign-in
> [!NOTE]
> The **Android Restore Credentials API** is mandatory for apps with sign-in. Since this app currently has no sign-in flow, this specific API integration is not required. If you plan to add accounts in the future, we will need to implement the Credential Manager.

## Verification Plan

### Manual Verification
- **Backup Check:** Use `adb shell bmgr backupnow <package>` to verify backup triggers correctly.
- **Memory Check:** Use the Android Studio Memory Profiler to monitor "Anonymous RSS" while the app is in the background.
- **DEX Optimization:** Run `./gradlew app:assembleRelease` and check the build analyzer to confirm R8 optimization is effective.
