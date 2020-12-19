<img alt="ChatEngine icon" src="https://i.imgur.com/HWvE23r.png" height=400>

`Version: 1.15+`\
`Core: paper or paper-based`\
`Depends: ProtocolLib`\
`Soft-depend: PlaceholderAPI`

ChatEngine is a paper-based plugin for make menus in the chat.\
Its help you make interaction between a server and players the better comfortable and beautiful!
Beautiful view with Hex and Gradients on 1.16+. Support placeholdersAPI for work with others
plugins. Convenient and understandable html tags system.

**Example menu view:**
<img alt="Menu gif" src="https://i.imgur.com/szuoY12.gif">

**Example menu configuration:**

```yaml
example-menu:
  command:
    name: examplemenu
    # Aliases is optional, can be removed.
    aliases: [exmenu, emenu]
    permission: examplemenu.perm
  pages:
    # First page without the depend of name is a main and open on command execute.
    main:
      - '<extend/34><#7FFFD4>Main menu'
      - '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
      - ''
      - '<extend/10>&eWelcome, &a%target%&e!'
      - '<extend/>'
      - '<extend/5><click run>chp(support)</click>&6> &eSupport'
      - ''
      - '<extend/5><click run>chp(resources)</click>&6> &eResources'
      - ''
      - '<extend/>'
      - '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
      - '<extend/71><click run>close</click>&c⟨Close⟩'

    support:
      - '<extend/34><#7FFFD4>Support'
      - '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
      - '<extend/>'
      - ''
      - '<extend/5>&eClick for open link:'
      - ''
      - '<extend/5><click open_url>https://ko-fi.com/alofi11</click>&6> &aKo-fi &e(paypal).'
      - ''
      - '<extend/5><click open_url>https://new.donatepay.ru/@658082</click>&6> &aDonatePay &e(other).'
      - '<extend/>'
      - '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
      - '<extend/72><click run>chp(main)</click>&c⟨Main⟩'

    resources:
      - '<extend/33><#7FFFD4>Resources'
      - '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
      - '<extend/>'
      - ''
      - '<extend/5>&eClick for open link:'
      - ''
      - '<extend/5><click open_url>https://github.com/alofi11/ChatEngine</click>&6> Git&8Hub'
      - ''
      - '<extend/5><click open_url>https://discord.gg/BcSgjrTbJg</click>&6> &5Discord'
      - '<extend/>'
      - '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
      - '<extend/72><click run>chp(main)</click>&c⟨Main⟩'
```