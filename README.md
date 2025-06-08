# 🐞 BugTracker

**BugTracker** is a lightweight and easy-to-use bug reporting plugin for Minecraft servers. It allows players to report bugs directly in-game, helping staff and developers track issues quickly without leaving the server.

---

## ✨ Features

- In-game bug reporting with `/bug` commands
- Flatfile support (`reports.yml`)
- Optional MySQL database storage
- View, delete, and search reports with permissions
- Cooldowns and word limits to prevent spam
- Customizable messages and date formatting

---

## 🔧 Configuration

### `config.yml`

```yaml
database: 
  enabled: false
  table_name: 'bugtracker_Bugs'
  host: null
  port: 1
  username: root
  password: password
  database_name: database

global:
  cooldown_in_seconds: 5
  minimum_words_to_submit: 4
  date_format: 'dd/MM/yy HH:mm:ss'

messages:
  errors:
    no_perms: '&cYou do not have permission to execute this command.'
    not_a_number: '&4&lError &cThat is not a number.'
    cooldown_activated: '&4Please way %seconds% seconds before submitting another report!'
    specifiy_a_player: '&4Please specify a player.'
  bugs:
    list: '&7[&c%id%&7] &f%report% &7- &c%creator%'
    submitted: '&aReport submitted!'
    needs_more_words: '&4&lError &cPlease specify the issue with at least %count% words.'
    report_deleted: '&aReport &l%id% &ahas been deleted!'
    no_reported_bugs: '&aNo bugs reported!'
    report_does_not_exist: '&cReport &l%id% &cdoes not exist!'
    no_reports_for_player: '&aNo reports from &l%player%&a!'
    total_reports: '&7Total reports: &c%reports%'
    pages: '&7Page &c%page%&7/&c%total%'
  reportinfo:
    - '&c&lBug Report &7&l%id%'
    - '&cReporter: &7%player%'
    - '&cReport: &7%report%'
    - '&cCreated: &7%created%'
    - '&cPosition: World:&7%world%, &cX:&7%x%, &cY:&7%y%, &cZ:&7%z%'
```

- **Database**: Enable and configure MySQL support (optional)
- **Cooldown**: Set delay between reports to prevent spam
- **Minimum Words**: Enforce quality by requiring detailed reports
- **Date Format**: Customize how timestamps are shown

---

## 💬 Commands & Permissions

| Command | Description | Permission |
|--------|-------------|------------|
| `/bug add <message>` or `/bug create <message>` | Submit a bug report | `bugtracker.use` |
| `/bug remove <id>` or `/bug delete <id>` | Delete a bug report | `bugtracker.modify` |
| `/bug list` | View all bug reports | `bugtracker.show` |
| `/bug player <player>` | View reports submitted by a specific player | `bugtracker.show` |
| `/bug show <id>` or `/bug info <id>` or `/bug detail <id>` | View detailed info for a specific bug report | `bugtracker.show` |
| `/bug help` or `/bug ?` | Show help information | `bugtracker.use` |

---

## 📁 Storage

You can choose between:
- 📄 Flatfile (`plugins/BugTracker/reports.yml`)
- 🗃️ MySQL Database (enable in config and set connection info)

---

## 🛠 Example Output

```txt
[BugTracker] [3] Player can't open chest near spawn - Steve
[BugTracker] Report submitted!
[BugTracker] Report 3 has been deleted!
```

Bug detail:
```
Bug Report #3
Reporter: Steve
Report: Player can't open chest near spawn
Created: 07/06/25 03:10:52
Position: World: world, X: 132, Y: 64, Z: 245
```

---

## 🔒 Permissions Overview

- `bugtracker.use` – For submitting and getting help
- `bugtracker.modify` – For deleting bug reports
- `bugtracker.show` – For viewing reports and details

---

## 👤 Author

Made by [SynicallyEvil](https://github.com/SynicallyEvil)  
If this helps your server stay bug-free, leave a ⭐ on GitHub!
