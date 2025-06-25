# GalaxyPPG-Logger

## Getting Started

Installing an app on the Galaxy Watch using just an APK file is restricted, and typically requires distribution through official channels such as the app store.
However, due to challenges in store distribution, please follow the steps below to install the app using **Android Studio**.

---

### Import Samsung Health Sensor SDK

Samsung Health Sensor SDK can be downloaded from the following link.
After downloading the AAR file, add it to your project under the `/wearable/libs` directory.
👉 [Samsung Health Sensor SDK](https://developer.samsung.com/health/sensor/overview.html)

---

### Preparing for Wireless Debugging

You can install apps remotely on your Galaxy Watch via Wi-Fi using Android Studio.
Please refer to the official guide below for setup instructions:
👉 [Wireless Debugging Guide](https://developer.android.com/training/wearables/get-started/debugging?hl=ko)

---

### Permission Control

To officially deploy and use this app with Samsung Health features, you must submit a partner request:
👉 [Samsung Health Partner Application](https://developer.samsung.com/SHealth/partner/MzgxNjUw)

However, for development or trial use, this app leverages **Health Developer Mode**, allowing you to bypass the partner approval process. In the wearable app, go to Settings > Applications > Health Platform and tap the top area multiple times to enable [Dev mode].

---

### App Features

At the top of each device, you can see wearable information (the wearable shows its own info, while the smartphone shows the connected wearable's info). Please ensure that both devices display identical information. If they don't match, you should first connect them through the [Galaxy Wearables](https://play.google.com/store/apps/details?id=com.samsung.android.app.watchmanager&pli=1) app.

**Wearable Device Features:**
- Play & Pause button for turning data collection on and off
- Flush button for deleting previously collected data
- Send button for sending collected data to the connected smartphone

**Smartphone Features:**
- Monitor the data collection status of the currently connected wearable device
- Receive and export data from the wearable device (Export button located in the menu bar)
- Tagging functionality to record timestamps of specific events
- Timer to track elapsed time during experiments
- Reset tagging info and timer (Reset button located in the menu bar)

---

## Reference for Using This App

If you use this app for research purposes, we kindly ask that you cite the following paper:

> **Park, S., Zheng, D. & Lee, U.** (2025).
> *A PPG Signal Dataset Collected in Semi-Naturalistic Settings Using Galaxy Watch*.
> *Scientific Data*, 12, 892. [https://doi.org/10.1038/s41597-025-05152-z](https://doi.org/10.1038/s41597-025-05152-z)

---

## Extending the Mobile-Sensing App

GalaxyPPG-Logger is designed with extensibility in mind and supports integrated collection of various mobile and wearable sensor data.
If you are interested in extending or collaborating on this project, please visit the GitHub repository below:
👉 [GitHub Repository – android-tracker](https://github.com/Kaist-ICLab/android-tracker)

