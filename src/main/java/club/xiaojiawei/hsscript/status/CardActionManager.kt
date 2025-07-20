package club.xiaojiawei.hsscript.status

import club.xiaojiawei.hsscriptcardsdk.CardAction
import club.xiaojiawei.hsscriptbase.bean.LikeTrie
import club.xiaojiawei.hsscriptbase.config.log
import club.xiaojiawei.hsscript.status.PluginManager.CARD_ACTION_PLUGINS
import club.xiaojiawei.hsscript.status.PluginManager.loadCardProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import java.util.function.Supplier

/**
 * @author 肖嘉威
 * @date 2024/9/7 16:23
 */
object CardActionManager {
    /**
     * key1：pluginId
     * key2：cardId
     */
    val CARD_ACTION_MAP: MutableMap<String, LikeTrie<Supplier<CardAction>>> = FXCollections.observableMap(load())

    init {
        loadCardProperty().addListener { _: ObservableValue<out Boolean>?, _: Boolean?, t1: Boolean ->
            if (t1) {
                reload()
            }
        }
    }

    private fun load(): MutableMap<String, LikeTrie<Supplier<CardAction>>> {
        return CARD_ACTION_PLUGINS.mapValues { entry ->
            val likeTrie = LikeTrie<Supplier<CardAction>>()

            val list = entry.value.flatMap { pluginWrapper ->
                // 添加监听器，当状态变化时重新加载
                pluginWrapper.addEnabledListener { _: ObservableValue<out Boolean?>?, _: Boolean?, _: Boolean? ->
                    reload()
                }
                // 只保留启用的插件实例
                if (pluginWrapper.isEnabled()) pluginWrapper.spiInstance else emptyList()
            }
            for (cardAction in list) {
                // 将每个 CardAction 生成的 Supplier 添加到 LikeTrie
                val idArray = cardAction.getCardId()
                for (cardId in idArray) {
                    likeTrie[cardId] = Supplier { cardAction.createNewInstance() }
                }
            }

            likeTrie
        }.toMutableMap()
    }

    private fun reload() {
        log.info { "刷新卡牌库" }
        CARD_ACTION_MAP.clear()
        CARD_ACTION_MAP.putAll(load())
    }
}
