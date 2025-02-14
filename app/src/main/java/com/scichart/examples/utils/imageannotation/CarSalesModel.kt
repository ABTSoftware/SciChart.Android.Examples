//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2024. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CarSalesModel.kt is part of SCICHART®, High Performance Scientific Charts
// For full terms and conditions of the license, see http://www.scichart.com/scichart-eula/
//
// This source code is protected by international copyright law. Unauthorized
// reproduction, reverse-engineering, or distribution of all or any portion of
// this source code is strictly prohibited.
//
// This source code contains confidential and proprietary trade secrets of
// SciChart Ltd., and should at no time be copied, transferred, sold,
// distributed or made available without express written permission.
//******************************************************************************

package com.scichart.examples.utils.imageannotation

import androidx.annotation.DrawableRes
import com.scichart.examples.R

data class CarSalesModel(
    val country: String,
    val carsSold: Int,
    val topBrands: List<String>,
    @DrawableRes val icon: Int
)

object CarSales {
    fun getData(): List<CarSalesModel> {
        return listOf(
            CarSalesModel(
                country = "China",
                carsSold = 30094767,
                topBrands = listOf("Volkswagen", "Toyota", "Geely"),
                icon = R.drawable.flag_cn
            ),
            CarSalesModel(
                country = "United States",
                carsSold = 15604278,
                topBrands = listOf("Ford", "Toyota", "Chevrolet"),
                icon = R.drawable.flag_us
            ),
            CarSalesModel(
                country = "Japan",
                carsSold = 4779639,
                topBrands = listOf("Toyota", "Suzuki", "Daihatsu"),
                icon = R.drawable.flag_jp
            ),
            CarSalesModel(
                country = "India",
                carsSold = 4108263,
                topBrands = listOf("Maruti Suzuki", "Hyundai", "Tata"),
                icon = R.drawable.flag_in
            ),
            CarSalesModel(
                country = "Germany",
                carsSold = 2845764,
                topBrands = listOf("Volkswagen", "Mercedes", "Audi"),
                icon = R.drawable.flag_de
            ),
            CarSalesModel(
                country = "Brazil",
                carsSold = 2309243,
                topBrands = listOf("Fiat", "Volkswagen", "Chevrolet"),
                icon = R.drawable.flag_br
            ),
            CarSalesModel(
                country = "United Kingdom",
                carsSold = 1905522,
                topBrands = listOf("Volkswagen", "Ford", "Audi"),
                icon = R.drawable.flag_gb
            ),
            CarSalesModel(
                country = "France",
                carsSold = 1776921,
                topBrands = listOf("Renault", "Peugeot", "Citroen"),
                icon = R.drawable.flag_fr
            ),
            CarSalesModel(
                country = "Canada",
                carsSold = 1664327,
                topBrands = listOf("Ford", "Toyota", "Chevrolet"),
                icon = R.drawable.flag_ca
            ),
            CarSalesModel(
                country = "Italy",
                carsSold = 1568623,
                topBrands = listOf("Fiat", "Volkswagen", "Toyota"),
                icon = R.drawable.flag_it
            )
        )
    }
}
