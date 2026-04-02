package pw.tmpim.goodassetfetcher.api

import net.modificationstation.stationapi.api.util.Namespace

interface GoodAssetFetcherRegistry {
  /**
   * Requests to fetch the given source asset from the given game version. The destination path will be replaced with
   * a StationAPI-compatible path if one is not provided. For example: `assets/minecraft/textures/blocks/stone.png` will
   * be saved to `assets/your-mod/stationapi/textures/block/stone.png`.
   *
   * @param namespace The mod namespace to save the asset to. Should be your mod's namespace.
   * @param gameVersion The version of the game according to the launcher metadata, e.g. `1.12.2`.
   * @param sourcePath The path of the asset within the game's jar, e.g. `assets/minecraft/textures/blocks/stone.png`.
   * @param destPath The path of the asset within good-asset-fetcher's generated resource pack, e.g.
   *   `assets/your-mod/stationapi/textures/block/stone.png`.
   */
  fun addResourceFile(
    namespace: Namespace,
    gameVersion: String,
    sourcePath: String,
    destPath: String,
  )

  /**
   * Requests to fetch the given asset from the given game version. The destination path will be replaced with
   * a StationAPI-compatible path. For example: `assets/minecraft/textures/blocks/stone.png` will be saved to
   * `assets/your-mod/stationapi/textures/block/stone.png`.
   *
   * @param namespace The mod namespace to save the asset to. Should be your mod's namespace.
   * @param gameVersion The version of the game according to the launcher metadata, e.g. `1.12.2`.
   * @param path The path of the asset within the game's jar, e.g. `assets/minecraft/textures/blocks/stone.png`.
   */
  fun addResourceFile(
    namespace: Namespace,
    gameVersion: String,
    path: String,
  )

  /**
   * Requests to fetch the given block texture from the given game version. The texture will be saved to a StationAPI-
   * compatible path. For example, if `name` is `stone`, it will fetch `assets/minecraft/textures/blocks/stone.png`
   * and save it to `assets/your-mod/stationapi/textures/block/stone.png`.
   *
   * @param namespace The mod namespace to save the asset to. Should be your mod's namespace.
   * @param gameVersion The version of the game according to the launcher metadata, e.g. `1.12.2`.
   * @param name The name of the block texture within the game's jar, e.g. `stone` will search for
   *   `assets/minecraft/textures/blocks/stone.png`.
   */
  fun addBlock(
    namespace: Namespace,
    gameVersion: String,
    name: String,
  )

  /**
   * Requests to fetch the given item texture from the given game version. The texture will be saved to a StationAPI-
   * compatible path. For example, if `name` is `stone`, it will fetch `assets/minecraft/textures/items/stick.png`
   * and save it to `assets/your-mod/stationapi/textures/item/stick.png`.
   *
   * @param namespace The mod namespace to save the asset to. Should be your mod's namespace.
   * @param gameVersion The version of the game according to the launcher metadata, e.g. `1.12.2`.
   * @param name The name of the item texture within the game's jar, e.g. `stick` will search for
   *   `assets/minecraft/textures/items/stick.png`.
   */
  fun addItem(
    namespace: Namespace,
    gameVersion: String,
    name: String,
  )
}
