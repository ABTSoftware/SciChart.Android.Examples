//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2022. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// UpdatedPointTransformation.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.utils.populationpyramid

import com.scichart.charting.visuals.animations.BaseRenderPassDataTransformation
import com.scichart.charting.visuals.animations.TransformationHelpers
import com.scichart.charting.visuals.renderableSeries.data.StackedColumnRenderPassData
import com.scichart.core.model.FloatValues

class UpdatedPointTransformation : BaseRenderPassDataTransformation<StackedColumnRenderPassData>(StackedColumnRenderPassData::class.java) {
    private val previousYCoordinates = FloatValues()
    private val previousPrevSeriesYCoordinates = FloatValues()

    private val originalYCoordinates = FloatValues()
    private val originalPrevSeriesYCoordinates = FloatValues()

    override fun saveOriginalData() {
        if (!renderPassData.isValid) return

        TransformationHelpers.copyData(renderPassData.yCoords, originalYCoordinates)
        TransformationHelpers.copyData(renderPassData.prevSeriesYCoords, originalPrevSeriesYCoordinates)
    }

    override fun applyTransformation() {
        if (!renderPassData.isValid) return

        val count = renderPassData.pointsCount()
        val currentTransformationValue = currentTransformationValue
        if (previousPrevSeriesYCoordinates.size() != count ||
            previousYCoordinates.size() != count ||
            originalYCoordinates.size() != count ||
            originalPrevSeriesYCoordinates.size() != count) return

        for (i in 0 until count) {
            val startYCoord = previousYCoordinates[i]
            val originalYCoordinate = originalYCoordinates[i]
            val additionalY = startYCoord + (originalYCoordinate - startYCoord) * currentTransformationValue

            val startPrevSeriesYCoords = previousPrevSeriesYCoordinates[i]
            val originalPrevSeriesYCoordinate = originalPrevSeriesYCoordinates[i]
            val additionalPrevSeriesY = startPrevSeriesYCoords + (originalPrevSeriesYCoordinate - startPrevSeriesYCoords) * currentTransformationValue

            renderPassData.yCoords[i] = additionalY
            renderPassData.prevSeriesYCoords[i] = additionalPrevSeriesY
        }
    }

    override fun discardTransformation() {
        TransformationHelpers.copyData(originalYCoordinates, renderPassData.yCoords)
        TransformationHelpers.copyData(originalPrevSeriesYCoordinates, renderPassData.prevSeriesYCoords)
    }

    override fun onInternalRenderPassDataChanged() {
        applyTransformation()
    }

    override fun onAnimationEnd() {
        super.onAnimationEnd()

        TransformationHelpers.copyData(originalYCoordinates, previousYCoordinates)
        TransformationHelpers.copyData(originalPrevSeriesYCoordinates, previousPrevSeriesYCoordinates)
    }
}