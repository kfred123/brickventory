package com.example.brickserver.service.bricks

import java.awt.image.BufferedImage
import org.apache.pdfbox.contentstream.PDFStreamEngine
import org.apache.pdfbox.contentstream.operator.DrawObject
import org.apache.pdfbox.contentstream.operator.Operator
import org.apache.pdfbox.cos.COSBase
import org.apache.pdfbox.cos.COSName
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject

data class ExtractedImage(
        val image: BufferedImage,
        val x: Float,
        val y: Float,
        val width: Float,
        val height: Float
)

class PdfImageExtractor : PDFStreamEngine() {
    val extractedImages = mutableListOf<ExtractedImage>()

    init {
        addOperator(DrawObject(this))
    }

    override fun processOperator(operator: Operator, operands: MutableList<COSBase>) {
        if (operator.name == "Do") {
            val objectName = operands[0] as COSName
            val xobject = resources.getXObject(objectName)

            if (xobject is PDImageXObject) {
                val ctm = graphicsState.currentTransformationMatrix

                // transform (0,0) and (1,1) to get the location and size in user space
                // images are drawn in a 1x1 box and transformed by the CTM
                val x = ctm.translateX
                val y = ctm.translateY

                // Scaling factors are typically in the m00 (scaleX) and m11 (scaleY) positions
                // But with rotation it's more complex. Assuming mostly axis-aligned for now.
                // Or simply: vector (1,0) mapped to (m00, m10) and (0,1) to (m01, m11)

                // Simply taking the scaling components for width/height approximation
                val width = ctm.scalingFactorX
                val height = ctm.scalingFactorY

                try {
                    val image = xobject.image
                    extractedImages.add(ExtractedImage(image, x, y, width, height))
                } catch (e: Exception) {
                    // Ignore images that fail to read
                    println("Failed to read image: ${e.message}")
                }
            }
        }
        super.processOperator(operator, operands)
    }
}
