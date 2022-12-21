package com.example.myaccountbook

import androidx.annotation.DrawableRes
import com.example.myaccountbook.R

enum class SpendCategoryEnum (
    val title: String,
    @DrawableRes val defaultRes: Int,
    @DrawableRes val selectedRes: Int)
{
    TRAFFIC(
        "교통",
        R.drawable.ic_category_transportation_disable_32dp,
        R.drawable.ic_category_transportation_32dp
    ),
    SLEEP(
        "여가",
        R.drawable.ic_category_hotel_disable_32dp,
        R.drawable.ic_category_hotel_32dp
    ),
    FOOD(
        "식비",
        R.drawable.ic_category_meal_disable_32dp,
        R.drawable.ic_category_meal_32dp
    ),
    SNACK(
        "간식",
        R.drawable.ic_category_snack_disable_32dp,
        R.drawable.ic_category_snack_32dp
    ),
    SHOPPING(
        "쇼핑",
        R.drawable.ic_category_shopping_disable_32dp,
        R.drawable.ic_category_shopping_32dp
    ),
    CULTURE(
        "문화",
        R.drawable.ic_category_culture_disable_32dp,
        R.drawable.ic_category_culture_32dp
    ),
    ETC(
        "기타",
        R.drawable.ic_category_etc_disable_32dp,
        R.drawable.ic_category_etc_32dp
    );

    companion object {
        fun getEnumByTitle(title: String): SpendCategoryEnum {
            return when (title) {
                "식비" -> FOOD
                "문화" -> CULTURE
                "교통" -> TRAFFIC
                "쇼핑" -> SHOPPING
                "숙박" -> SLEEP
                "간식" -> SNACK
                "기타" -> ETC

                else -> ETC
            }
        }
    }
}