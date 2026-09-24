# 🐾 PawConnect — Pet Adoption Management System
**Course Code:** 21CSC206P — Object Oriented Programming & Java  
**Architecture:** Model-View-Controller (MVC) Desktop Java Swing Application  
**Mode:** Pure Standalone GUI (Zero External Database Dependency Required)  
**Security Model:** Role-Based Access Control (Adopter Portal vs. Shelter Administrator Portal)  
**Platform:** Java 17+ (Standard JDK `javac` / `java`)

---

## 📌 Executive Summary

**PawConnect** is an enterprise-grade desktop **Pet Adoption Management System** built with **100% Java Swing** and standard Object-Oriented principles. It features complete role segregation separating **Adopter** responsibilities from **Shelter Staff / Administrator** operations.

---

## 👥 Role Separation: Adopter vs. Shelter Administrator

The application dynamically tailors the interface and capabilities based on the authenticated portal:

### 1. 🐶 Adopter Portal (Prospective Pet Parent)
*Logged in as: Rahul Sharma (`adopter` / `user123`)*

Adopters only see features related to discovering, matching, and applying for pets:
1. **Browse Adoptable Animals:**
   - **Visual Pet Cards Gallery** (`GridLayout`): View pet photos, temperament tags (`Kids OK`, `Dogs OK`, `House Trained`), energy rating, and status badges.
   - **Data Table View** (`JTable`): Sortable columns with live multi-attribute search filtering.
   - **Pet Profile Inspector:** Full vital statistics, behavioral requirements, and direct `[ Apply for Adoption ]` action.
   - *Administrative controls (+ Add Pet, Edit, Delete) are completely hidden.*
2. **Smart Compatibility Matcher:**
   - Heuristic questionnaire (Housing type, fenced yard, energy level, children, other pets, hours away).
   - Real-time compatibility ranking with `JProgressBar` breakdown, positive factors, and caution notes.
3. **My Applications & Status Tracker:**
   - Review personal submitted petitions.
   - View real-time status (`PENDING REVIEW`, `UNDER REVIEW`, `APPROVED`, `REJECTED`).
   - Read official shelter feedback notes and next steps.
   - Ability to withdraw pending petitions.
4. **Pet Health & Clinical Cards:**
   - Read-only review of vaccination history and clinical checkup dates.

---

### 2. 🏢 Shelter Administrator Portal (Staff Management)
*Logged in as: Dr. Sarah Jenkins (`admin` / `admin123`)*

Shelter staff see operational management, inventory control, and legal petition adjudication:
1. **Census & Operations Dashboard:**
   - Real-time KPI summary cards (`GridLayout`): Total Registry, Available for Placement, Successful Adoptions, Pending Petitions, and Clinical Quarantine.
2. **Manage Pet Inventory (Full CRUD):**
   - `[ + Add Pet Intake ]`: Open precision `GridBagLayout` registration modal.
   - `[ Edit Pet Record ]`: Update physical traits, behavioral tags, and adoption status.
   - `[ Delete Selected ]`: Permanent deletion with cascade confirmation dialog.
3. **Adoption Petitions Queue (Adjudication):**
   - Review incoming petitions submitted by adopters.
   - Double-click or click `[ Review & Adjudicate ]` to inspect applicant profile and living situation.
   - Formal decision options: `APPROVED`, `UNDER_REVIEW`, `REJECTED`, or `WITHDRAWN` with review notes.
   - **Atomic State Synchronization:** Approving an application automatically updates the pet status to `ADOPTED`.
4. **Veterinary & Clinical Logs:**
   - Log medical exams, surgical procedures, and vaccinations.
   - Attending clinician assignment and next due date scheduling.
   - Permanent clinical log deletion.

---

## 🎯 21CSC206P Syllabus Concept Mapping

| Syllabus Concept | PawConnect Implementation | Primary Class / Location |
| :--- | :--- | :--- |
| **Top-Level Window (`JFrame`)** | Role-tailored windows: `LoginFrame` (Initial window), `MainFrame` (Portal workspace) | [`LoginFrame.java`](file:///C:/Users/saima/OneDrive/Documents/java_project/src/org/pawconnect/view/LoginFrame.java), [`MainFrame.java`](file:///C:/Users/saima/OneDrive/Documents/java_project/src/org/pawconnect/view/MainFrame.java) |
| **Modal Secondary Windows (`JDialog`)** | `PetProfileDialog`, `PetEntryDialog`, `ApplicationDialog`, `ReviewApplicationDialog`, `MedicalEntryDialog` | Dialog classes in `org.pawconnect.view.dialogs` |
| **Containers & Organization** | `JSplitPane` (Master/Detail), `JTabbedPane` (Workspaces), `JScrollPane`, `PetCardGridPanel` | View panels in `org.pawconnect.view` |
| **Desktop Menus & Toolbars** | `JMenuBar`, `JMenu`, `JMenuItem`, `JSeparator`, `JToolBar` tailored to active role | [`MainFrame.java`](file:///C:/Users/saima/OneDrive/Documents/java_project/src/org/pawconnect/view/MainFrame.java) |
| **Data Grids (`JTable`)** | Dynamic inventory & petition tables, custom `TableCellRenderer` for status badges, alternating row colors, `DefaultTableModel` | [`TableFormatters.java`](file:///C:/Users/saima/OneDrive/Documents/java_project/src/org/pawconnect/view/util/TableFormatters.java) |
| **Form Components** | `JTextField`, `JPasswordField`, `JComboBox`, `JRadioButton`, `ButtonGroup`, `JCheckBox`, `JTextArea`, `JSpinner`, `JSlider` | Dialog forms & [`SmartMatchPanel.java`](file:///C:/Users/saima/OneDrive/Documents/java_project/src/org/pawconnect/view/SmartMatchPanel.java) |
| **Progress & Metrics** | `JProgressBar` rendering compatibility score (0-100%) with dynamic color coding | [`SmartMatchPanel.java`](file:///C:/Users/saima/OneDrive/Documents/java_project/src/org/pawconnect/view/SmartMatchPanel.java) |
| **Layout Managers** | `BorderLayout` (Frame skeletons), `GridBagLayout` (Forms & details), `GridLayout` (Pet cards & KPI cards), `FlowLayout` (Toolbars & chips) | Used throughout the `view` package |
| **Event Delegation Model** | `ActionListener`, `DocumentListener` (live table search), `ListSelectionListener`, `MouseAdapter` (double-click row adjudication), `WindowAdapter` | All view and dialog components |
| **MVC Architecture** | • **Model:** `Pet`, `Adopter`, `AdoptionApplication`, `MedicalRecord`, `MatchResult`.<br>• **View:** Swing GUI classes in `org.pawconnect.view`.<br>• **Controller:** `PetController`, `ApplicationController`, `MatchingEngine`, `AuthController`. | Clean package separation under `org.pawconnect` |
| **Data Layer (In-Memory Repository)** | Pre-seeded thread-safe memory repository simulating database operations (auto-increment IDs, filtering, status updates) without requiring any external database setup. | [`InMemoryDatabase.java`](file:///C:/Users/saima/OneDrive/Documents/java_project/src/org/pawconnect/dao/InMemoryDatabase.java), [`PetDAO.java`](file:///C:/Users/saima/OneDrive/Documents/java_project/src/org/pawconnect/dao/PetDAO.java) |

---

## ⚡ How to Build and Run (Zero External Dependencies)

### 1. Compile the Project
Open terminal in `C:\Users\saima\OneDrive\Documents\java_project`:
```cmd
scripts\build.bat
```
*Expected Output:*
```
====================================================
Compiling PawConnect Pet Adoption Management System
(Pure Standalone Java Swing GUI Mode)
====================================================
[SUCCESS] Compilation completed without errors.
```

### 2. Launch the Application
```cmd
scripts\run.bat
```

1. **Authentication:** The centered **`LoginFrame`** will appear with two portal options.
2. **Role Selection & Sign In:**
   - Select **`[ Adopter Portal ]`** and click **`Sign In to PawConnect`** to access pet discovery, smart matching, and application tracking (credentials: `adopter` / `user123`).
   - Select **`[ Shelter Admin Portal ]`** and click **`Sign In to PawConnect`** to access operations dashboard, pet inventory CRUD, adjudication queue, and medical logs (credentials: `admin` / `admin123`).
   - *Tip:* You can also simply press **Enter** in the password field to submit.
3. **Sign Out:** Click **`Sign Out`** in the top toolbar to return to the `LoginFrame` and switch roles at any time.

### 3. Run Automated Sanity & Regression Suite
```cmd
java -ea -cp "bin" org.pawconnect.TestRunner
```
*Executes all 5 automated test suites verifying search filters, algorithmic compatibility scoring, and application adjudication.*
