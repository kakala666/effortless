![Logo](assets/logo.png)

# 轻松建造

[轻松建造]() 是一个用于在 Minecraft 中放置和破坏方块的多平台 Mod。它提供了一组模式,让玩家可以轻松创建结构和设计。此外,它还包含一个随机选择器,可以从预定义列表中随机选择物品并进行随机放置。

<div style="text-align: center">
    <a href="https://modrinth.com/mod/effortless">Modrinth</a>
    <span> | </span>
    <a href="https://www.curseforge.com/minecraft/mc-mods/effortless">CurseForge</a>
    <span> | </span>
    <a href="https://github.com/kakala666/effortless">GitHub</a>
    <span> | </span>
    <a href="https://github.com/kakala666/effortless/wiki">文档</a>
</div>


## 致谢

本 Mod 基于 [**Huskcasaca**](https://github.com/huskcasaca) 所开发的 [Effortless Structure](https://github.com/huskuraft/effortless) 修改而来。但原作者已停止维护,本分支在其代码基础上修复了已知问题并继续维护。

向原作者 **Huskcasaca** 致以诚挚的感谢——没有他多年的辛勤投入,就没有这个 Mod。

## 功能

- **纯原版兼容性**:该 Mod 设计为完全兼容纯原版游戏,不会添加新物品,也不会进行不兼容的修改。
- **物品随机选择器**:该 Mod 包含一个物品随机选择器,允许玩家从预定义列表中随机放置方块和实体。
- **剪贴板**:该 Mod 包含一个剪贴板,允许玩家在不同世界之间复制和粘贴方块与实体。

## 平台

- 你需要在客户端和服务器端都安装此 Mod。
- 你可以在服务器平台与你的客户端平台不同的情况下使用此 Mod。
- 同一个 Mod jar 文件可以用于多个目标平台。

> **本分支目前仅支持 NeoForge。** 未来会逐步恢复对Fabric和Forge的支持。

### 目标版本

| 文件名                        | 适用版本 | NeoForge |
|-------------------------------|----------|:--------:|
| `effortless-1.21.1-3.4.0.jar` | `1.21.1` | &check;  |

### 兼容插件

- 你可以将此 Mod 与下列插件配合使用。

| 名称                                                                        |   支持   | 说明                           |
|-----------------------------------------------------------------------------|:--------:|--------------------------------|
| [Open Parties and Claims](https://modrinth.com/mod/open-parties-and-claims) | &check;  | 允许你声明区块并设置建造权限。 |
| [FTB Chunks](https://www.curseforge.com/minecraft/mc-mods/ftb-chunks-forge) | &check;  | 允许你声明区块并设置建造权限。 |

## 使用方法

- 按住 **LEFT_ALT / LEFT_OPTION** 键打开**建造模式径向菜单**,可在其中切换建造模式以创建不同结构。菜单左侧还有 **撤销 / 重做**、**替换**、**设置**、**图案** 与 **剪贴板** 的按钮。
- 按 **ATTACK / DESTROY** 键开始破坏方块。
- 按 **USE_ITEM / PLACE_BLOCK** 键开始放置/交互方块。
- 按 **LEFT_BRACKET**(`[`)键执行**撤销**,撤销上一次建造操作。
- 按 **RIGHT_BRACKET**(`]`)键执行**重做**,重做上一次撤销的操作。

### 建造模式

- 建造模式是创建结构时需要选择的基本形状。每种模式都有独特特性(如空心或实心)。

- **禁用**(Disabled):按原版方式放置。
- **单块**(Single):增加可达距离的单块放置。
- **直线**(Line):沿任一坐标轴放置一条直线。
- **墙面**(Wall):沿 X 或 Z 轴放置一面墙。
- **地板**(Floor):沿 Y 轴放置一层地板。
- **对角线**(Diagonal Line):沿任意角度放置一条直线。
- **对角墙**(Diagonal Wall):沿任意角度放置一面墙。
- **坡度地板**(Slope Floor):沿任意角度放置一个斜面。
- **方形**(Square):放置一个正方形平面。
- **长方体**(Cuboid):放置一个长方体。
- **圆形**(Circle):沿任一坐标轴放置一个圆。
- **圆柱体**(Cylinder):沿任一坐标轴放置一个圆柱。
- **球体**(Sphere):放置一个由方块组成的球体。
- **穹顶**(Dome):放置一个由方块组成的穹顶。
- **金字塔**(Pyramid):放置一个由方块组成的金字塔。
- **圆锥体**(Cone):放置一个由方块组成的圆锥。

### 替换

- 你可以选择放置新方块时如何替换原有方块。

- **禁用**(Disable):仅替换空气与可被覆盖的方块(如草丛)。
- **方块与空气**(Blocks and Air):替换空气,以及所有可被工具破坏的方块。
- **仅方块**(Blocks Only):仅替换可被工具破坏的方块。
- **仅副手**(Offhand Only):仅替换与副手中手持物品相同的方块。

### 图案

- 你可以通过组合不同的**转换器**(Transformer)来创建复杂形状。例如,用"镜像"为一面墙创建对称副本,或用"物品随机选择器"做出一面随机方块组成的墙。目前共有 4 种转换器。

- **镜像**(Mirror):为方块与实体生成对称镜像,用于偶数和奇数对称的建造。
- **阵列**(Array):沿指定方向将方块与实体复制指定次数。
- **径向**(Radial):围绕一个中心点以圆形图案放置方块与实体。圆可被等分成若干扇区,每个扇区都会包含一份放置副本。
- **物品随机选择器**(Item Randomizer):随机化方块的放置。

### 剪贴板

- 你可以使用剪贴板,在不同世界之间通过复制与粘贴来转移结构。

## 依赖

### NeoForge

| 依赖            | 下载链接                                   |
|-----------------|--------------------------------------------|
| NeoForge Loader | https://neoforged.net/categories/releases/ |

## 历代贡献者

* **[Requioss](https://www.curseforge.com/members/requioss)**,概念源头之作 [Effortless Building](https://www.curseforge.com/minecraft/mc-mods/effortless-building) 的作者。
* **[loehnertj](https://github.com/loehnertj)**,1.20.2 移植。
* **[Huskcasaca](https://github.com/huskcasaca)**,本 Mod(轻松建造)上游版本的作者。

## 许可证

轻松建造(Effortless Structure)以 [LGPLv3](LICENSE) 许可证发布。
