# BizFlow POS System 🍽️

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg)](https://android-arsenal.com/api?level=21)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A modern, feature-rich Android Point of Sale (POS) system designed specifically for restaurants, cafes, and food service businesses. Built with Material Design 3, offering a seamless table management experience with real-time order tracking and comprehensive business analytics.

## ✨ Features

### 🎯 Core Functionality
- **Table Management**: Visual table layout with real-time availability status
- **Order Management**: Running orders that persist across sessions
- **Smart Menu**: Category filtering, search, and voice search support
- **Payment Processing**: Multiple payment methods (Cash/Card/UPI) with discount and GST calculations
- **Thermal Printing**: Bluetooth printer integration with ESC/POS commands
- **Invoice Generation**: Professional invoices with complete order breakdown

### 📊 Business Intelligence
- **Real-time Dashboard**:
  - Today's sales and total orders
  - Best-selling items analytics
  - Tables in use monitoring
- **PDF Reports**: Daily and monthly sales reports with custom save locations
- **Sales History**: Complete transaction history with filtering

### 🎨 Modern UI/UX
- **Material Design 3**: Latest design guidelines with dynamic theming
- **Dark Mode**: Automatic system-based theme switching
- **Smooth Animations**: Cart interactions with scale and fade effects
- **Responsive Design**: Optimized for phones and tablets
- **Intuitive Navigation**: Table → Menu → Payment → Invoice workflow

### 🔒 Security & Administration
- **PIN Authentication**: Secure admin access (default: 1234)
- **Food Management**: Add, edit, delete menu items with images
- **Business Settings**: Customize business name, address, and printer settings
- **Table Management**: Configure table capacity and status

## 📱 Screenshots

<!-- Add your screenshots here -->
```
[Main Screen] [Table View] [Menu Screen] [Dashboard] [Reports]
```

## 🛠️ Tech Stack

- **Language**: Java
- **Min SDK**: 21 (Android 5.0 Lollipop)
- **Target SDK**: 34 (Android 14)
- **Architecture**: MVVM Pattern

### Libraries & Dependencies
```gradle
// UI & Design
implementation 'com.google.android.material:material:1.11.0'
implementation 'androidx.cardview:cardview:1.0.0'

// Database
implementation 'androidx.room:room-runtime:2.6.1'
annotationProcessor 'androidx.room:room-compiler:2.6.1'

// PDF Generation
implementation 'com.itextpdf:itext7-core:7.2.5'

// Core Android
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'androidx.recyclerview:recyclerview:1.3.2'
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog | 2023.1.1 or later
- JDK 17 or higher
- Android SDK with API 21+
- Bluetooth-enabled Android device for printer testing

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/bizflow-pos.git
   cd bizflow-pos
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory

3. **Sync Gradle**
   - Wait for Gradle sync to complete
   - Resolve any dependency issues if prompted

4. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ```
   Or use Android Studio's Run button (Shift + F10)

## 📖 Usage Guide

### First Time Setup
1. Launch the app - sample data will be automatically created
2. Navigate to Settings (3-dot menu) to configure:
   - Business name and address
   - Bluetooth thermal printer
   - Admin PIN (default: 1234)

### Daily Operations
1. **Taking Orders**:
   - Select a table from the main screen
   - Browse menu or use voice search
   - Add items using +/- buttons
   - Click "Print Invoice" when ready

2. **Payment Processing**:
   - Enter discount percentage (if applicable)
   - Adjust GST tax rate
   - Select payment method
   - Proceed to generate invoice

3. **Viewing Reports**:
   - Access Dashboard from the menu
   - Generate daily or monthly PDF reports
   - Choose save location via file picker

### Admin Functions (PIN Required)
- Food Management: Add/edit/delete menu items
- Table Management: Configure tables
- Change PIN: Update admin security PIN

## 🖨️ Bluetooth Printer Setup

1. **Pair Printer**:
   - Enable Bluetooth on your Android device
   - Pair with your thermal printer (58mm recommended)

2. **Configure in App**:
   - Go to Settings → Printer Settings
   - Select your paired printer
   - Test print to verify connection

3. **Supported Printers**:
   - Any ESC/POS compatible thermal printer
   - 58mm paper width (32 characters)
   - Bluetooth connectivity required

## 📁 Project Structure

```
app/
├── src/main/
│   ├── java/com/bizflow/pos/
│   │   ├── MainActivity.java          # Table view screen
│   │   ├── MenuActivity.java          # Food ordering screen
│   │   ├── DashboardActivity.java     # Analytics dashboard
│   │   ├── InvoiceActivity.java       # Invoice preview
│   │   ├── SettingsActivity.java      # App settings
│   │   ├── FoodManagementActivity.java # Menu management
│   │   ├── SalesHistoryActivity.java  # Transaction history
│   │   ├── PrinterManager.java        # Bluetooth printing
│   │   ├── AppDatabase.java           # Room database
│   │   └── [Entities, DAOs, Adapters]
│   ├── res/
│   │   ├── layout/                    # XML layouts
│   │   ├── values/                    # Themes, colors, strings
│   │   ├── values-night/              # Dark mode themes
│   │   ├── menu/                      # Menu resources
│   │   └── anim/                      # Animations
│   └── AndroidManifest.xml
└── build.gradle
```

## 🎨 Customization

### Changing Theme Colors
Edit `res/values/colors.xml` and `res/values-night/colors.xml`:
```xml
<color name="md_theme_light_primary">#006C4C</color>
<color name="md_theme_dark_primary">#6CDBAC</color>
```

### Adding Menu Categories
Categories are automatically generated from food items. Add items with different categories in Food Management.

### Modifying Receipt Format
Edit `PrinterManager.java` → `formatReceipt()` method:
```java
int paperWidth = 32; // Adjust for different paper sizes
```

## 🔧 Configuration

### Database Version
Current version: **4**

To modify database schema:
1. Update entity classes
2. Increment version in `AppDatabase.java`
3. Add migration strategy if needed

### SharedPreferences Keys
- `cafe_prefs`: Printer address
- `admin_settings`: Admin PIN
- `business_settings`: Business name/address
- `printer_settings`: Reconnect interval

## 🐛 Troubleshooting

### Printer Not Connecting
- Ensure Bluetooth is enabled
- Check printer is paired in system settings
- Verify printer is powered on
- Try reconnecting from Printer Settings

### PDF Not Saving
- Grant storage permissions when prompted
- Ensure sufficient storage space
- Check file picker permissions

### Database Errors
- Clear app data and restart
- Uninstall and reinstall app
- Check logcat for specific errors

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Coding Standards
- Follow Android coding conventions
- Add comments for complex logic
- Test on multiple devices/API levels
- Update README for new features

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Your Name**
- GitHub: [@yourusername](https://github.com/yourusername)
- LinkedIn: www.linkedin.com/in/javed-hallikeri3993
- Email: your.email@example.com

## 🙏 Acknowledgments

- Material Design 3 guidelines by Google
- iText PDF library for report generation
- Android Room persistence library
- ESC/POS printer command reference

## 📊 Project Stats

- **Lines of Code**: ~5,000+
- **Activities**: 10+
- **Database Tables**: 4 (Food Items, Sales, Tables, Table Orders)
- **Supported Languages**: English (easily extensible)

## 🔮 Future Enhancements

- [ ] Cloud sync for multi-device support
- [ ] Customer management system
- [ ] Inventory tracking
- [ ] Employee management
- [ ] Online ordering integration
- [ ] Multi-language support
- [ ] Advanced analytics with charts
- [ ] QR code table ordering

## 📞 Support

For issues, questions, or suggestions:
- Open an [Issue](https://github.com/yourusername/bizflow-pos/issues)
- Email: support@example.com
- Documentation: [Wiki](https://github.com/yourusername/bizflow-pos/wiki)

---

⭐ **If you find this project helpful, please consider giving it a star!** ⭐

**Made with ❤️ for the restaurant industry**
