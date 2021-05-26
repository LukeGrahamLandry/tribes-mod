# Tribes Mod 

A Minecraft mod for forge 1.16.5 that allows players to form tribes. Commissioned by Khaki.

## Features
- config at saves/world_name/serverconfig/tribes-server.toml
- tribe data stored at saves/world_name/data/tribes.json
- tribe compass item that points to the nearest claimed chunk
- /tribe 
    - create [name]
    - join [name]
    - count [name]
    - leave
    - delete [name] (leader only)
    - ban [player] (must be 1 rank higher)
    - unban [player] (officers+)
    - promote [player] (must be 2 ranks higher)
    - demote [player] (must be 1 rank higher)
    - [ally, enemy, neutral] [name] (vice leader+)
    - who [player]
    - initials [string] (vice leader+)
    - chunk [claim, unclaim] (officers+)
    - admin [save, load, delete, rename] (admins in config)
- GUIs
    - create tribe
    - choose effects (/tribe effects)
    - join tribe (/tribe join)
    - owner of current chunk displayed in top left

## Credits
- [Luke Graham Landry](https://lukegrahamlandry.ca)
- [GreenOne](https://github.com/TheGreenOne)
