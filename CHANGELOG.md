# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v3.1.0-1.18.1] - 2022-02-20
### Changed
- All players can now teleport to their death point, not just operators (this can be changed back in the config)
- To avoid abuse of this feature, a time limit can be set for how long after dying this is possible, also only the latest death point can be teleported to

## [v3.0.0-1.18.1] - 2021-12-13
- Ported to Minecraft 1.18
### Removed
- Removed death compass feature (split off into [Death Compass] mod)
- Removed death maps feature
- Removed death item removal feature (it never really worked as intended anyways)

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
[Death Compass]: https://www.curseforge.com/minecraft/mc-mods/death-compass-forge
