package club.xiaojiawei.hsscript.bean.log

import club.xiaojiawei.hsscript.enums.TagEnum

/**
 * @author 肖嘉威
 * @date 2022/11/29 14:24
 */

class TagChangeEntity(
    var tag: TagEnum? = null,
    var value: String = "",
    var parentBlock: Block? = null,
) : CommonEntity()
