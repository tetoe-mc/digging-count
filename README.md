# Digging Count

[![Author](https://img.shields.io/badge/Author-NriotHrreion-red.svg "Author")](https://github.com/NriotHrreion)
[![LICENSE](https://img.shields.io/badge/License-CC0_1.0-green.svg "LICENSE")](./LICENSE)
[![Stars](https://img.shields.io/github/stars/tetoe-mc/digging-count.svg?label=Stars&style=flat)](https://github.com/tetoe-mc/digging-count/stargazers)
[![Github Workflow Status](https://img.shields.io/github/actions/workflow/status/tetoe-mc/digging-count/build.yml)](https://github.com/tetoe-mc/digging-count/actions/workflows/build.yml)

## Description

This is a Minecraft Fabric plugin that provides digging count rankings via scoreboard. When player breaks a block, the player's score will increase 1.

Because the plugin is a server-side one, thus players don't have to install anything at the client-side to use the features provided by the plugin.

## Download

Check [Releases](https://github.com/tetoe-mc/digging-count/releases) for the jar file.

## Usage

Drag the downloaded jar file into the mod folder, launch your server, and the plugin is ready to work.

The scoreboard is put in the sidebar slot by default. And you can use the commands below to change it.

### Commands

You can use these commands to configure your digging count scoreboard.

- `/digging enable <slot>`: Enable the scoreboard in the specified slot.
- `/digging disable <slot>`: Disable the scoreboard in the specified slot.
- `/digging title <title>`: Change the title of the scoreboard.
- `/digging remove <player>`: Remove a specified player from the scoreboard.

## Build from source

```cmd
git clone https://github.com/tetoe-mc/digging-count.git
cd digging-count
./gradlew build
```

Then you can get the jar file in the build folder.

## LICENSE

[CC0 1.0](./LICENSE)
