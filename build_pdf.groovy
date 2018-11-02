
@Grab('com.github.dhorions:boxable:1.5') // depends on pdfbox
@Grab('org.yaml:snakeyaml:1.23')

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDType0Font

import be.quodlibet.boxable.BaseTable
import be.quodlibet.boxable.HorizontalAlignment
import be.quodlibet.boxable.VerticalAlignment

import org.yaml.snakeyaml.Yaml


def colPerPage = 4
def rowPerPage = 10

def wordsPerPage = colPerPage * rowPerPage

// page sizes
def pageHeight = PDRectangle.A4.height
def pageWidth = PDRectangle.A4.width

float rowHeight = pageHeight/rowPerPage
float cellWidthInPercent = 100/colPerPage

// load translates
def yaml = new Yaml()  as Object
def translations = yaml.load(new File("words.yaml").text)

// create pdf document and load font
def pdfDocument = new PDDocument() as Object
def font = PDType0Font.load(pdfDocument, new File("roboto.ttf"))

def translationsPerPage = translations.collate(wordsPerPage)

translationsPerPage.each { translationPerPage ->

    // render english words
    //
    def pageEn = new PDPage(PDRectangle.A4) as Object
    def baseTableEn = new BaseTable(0, pageHeight, 0, pageWidth, 0, pdfDocument, pageEn, true, true)  as Object

    def translationsPerRow = translationPerPage.collate(colPerPage)

    translationsPerRow.each { listForRow ->

        def row = baseTableEn.createRow(rowHeight)

        listForRow.each { translation ->

            def cell = row.createCell(cellWidthInPercent, translation.en, HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE)
            cell.setFont(font)
        }
    }
    baseTableEn.draw()


    // render russian words
    //
    def pageRu = new PDPage(PDRectangle.A4) as Object
    def baseTableRu = new BaseTable(0, pageHeight, 0, pageWidth, 0, pdfDocument, pageRu, true, true) as Object

    translationsPerRow.each { listForRow ->

        def row = baseTableRu.createRow(rowHeight)

        listForRow.reverseEach { translation ->

            def cell = row.createCell(cellWidthInPercent, translation.ru, HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE)
            cell.setFont(font)
        }
    }
    baseTableRu.draw()
}

// save to file
pdfDocument.save("words.pdf")

pdfDocument.close()



