# Good Farming 

![Minecraft Beta 1.7.3](https://img.shields.io/badge/minecraft-beta_1.7.3-70B237)
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/good-farming?logo=modrinth)](https://modrinth.com/mod/good-farming/)
![Maven Version](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo.lem.sh%2Freleases%2Fpw%2Ftmpim%2Fmygoodmods%2Fgood-farming%2Fmaven-metadata.xml)

<!-- modrinth_exclude.start -->
[![Download on Modrinth](../images/modrinth.png)](https://modrinth.com/mod/good-farming/)
<!-- modrinth_exclude.end -->

Beta 1.7.3 additions and tweaks to make farming easier.

Adds a Seed Bag, which stores up to 512 Seeds or Bone Meal, and plants on farmland in a 7×5×7 area by right-clicking
it on top of a block. The bag can also be used from further away than regular items (5 blocks). A Seed Bag can only hold 
one type of item, and cannot be emptied other than by using it.

Also nerfs crop trampling, replicating the behaviour of modern versions where you need to fall from a height to trample
the crops instead.

![Screenshot of Minecraft Beta 1.7.3 farm. The text reads 'Plant many items at once!', 'Seeds', 'Bone Meal', and 'Walk 
without trampling crops!'](/images/demo.png)

## Items

#### Seed Bag

![Recipe for the Seed Bag, which requires 1 string on top of 1 leather, and crafts 1 Seed Bag.](/images/recipe-seed-bag.png)

1x String + 1x Leather (shaped) → 1x Seed bag. Holds 512 seeds.

#### Seed Bag + Seeds

![Recipe for filling the Seed Bag with Seeds. Combine the Seed Bag shapelessly with any amount of Seeds to fill the Seed Bag.](/images/recipe-seed-bag-wheat-seeds.png)

1x Seed Bag + any amount of Seeds (shapeless) to fill the Seed Bag. Up to 512 Seeds.

#### Seed Bag + Bone Meal

![Recipe for filling the Seed Bag with Bone Meal. Combine the Seed Bag shapelessly with any amount of Bone Meal to fill the Seed Bag.](/images/recipe-seed-bag-bone-meal.png)

1x Seed Bag + any amount of Bone Meal (shapeless) to fill the Seed Bag. Up to 512 Bone Meal.

## Requirements

- Minecraft Beta 1.7.3
- [Babric](<https://babric.github.io/use/installer/>)
- [StationAPI](<https://modrinth.com/mod/stationapi>)
- [Fabric Language Kotlin](<https://modrinth.com/mod/fabric-language-kotlin>)

## Recommended

- [Mod Menu Babric](<https://modrinth.com/mod/modmenu-babric>) (for in-game configuration)

## Configuration

The mod's configuration can be configured in-game (if [Mod Menu Babric](<https://modrinth.com/mod/modmenu-babric>) is
installed) or in `.minecraft/config/good-farming/good-farming.yml`.

```yml
# Allow replanting crops with right-click (if you have the seeds)
quickReplantingEnabled: true

# If enabled, farmland is only trampled when jumping, not walking
tramplingNerfEnabled: true

# The lateral radius, in blocks, to plant seeds with the Seed Bag
seedBagPlantLateralRadius: 3

# The vertical radius, in blocks, to plant seeds with the Seed Bag
seedBagPlantVerticalRadius: 2

# The range, in blocks, that the Seed Bag can be used from
seedBagThrowRange: 5.0

# The maximum number of seeds the Seed Bag can store
seedBagCapacity: 512
```

## License

This mod is licensed under the [MIT license](../LICENSE). 
