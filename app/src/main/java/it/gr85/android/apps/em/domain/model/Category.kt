package it.gr85.android.apps.em.domain.model

data class Category (
    override val id: Id,
    val name: String,
    val color: Color
) : BaseObject(id) {

    init {
        require(
            name.isNotBlank()
        )

    }

}

