<p align="center">
   <img src="https://www.buildersrefuge.com/wp-content/uploads/2018/11/Builders-Refuge-Logo-Horizontal-RGB.png" height="150" align="center">
</p>
<p align="center">
   <a href="https://discord.gg/dCrMhk3">
   <img src="https://img.shields.io/discord/256198526248157186.svg?style=flat-square&logo=discord" alt="Discord Invite"/>
   </a>
   <a href="https://www.buildersrefuge.com/">
   <img src="https://img.shields.io/badge/Website-buildersrefuge.com-5eb7c1.svg?style=flat-square" alt="Website"/>
   </a>
</p>

---
# WorldCleaner

WorldCleaner is a utility to clean up worlds that have not been updated after a specific date. It will move the 
unchanged worlds to a dedicated location specified in the config file. It will also delete the world entry from the 
database to allow the world id to be reused.
 
 
### How it works
Every world contains a `level.dat` file and it is this file WorldCleaner checks. The `matchDate`specified 
 
### Folder structure
The target folder structured is 
``` 
worlds\
    -1,1\
     0,0\
    1,-1\
```
``` 
worlds\
    owner-uuid\
        -1,1\
        0,0\
        1,-1\
```
