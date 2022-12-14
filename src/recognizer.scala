import net.sourceforge.tess4j.*
import org.bytedeco.tesseract.global.tesseract
import scala.swing.*
import scala.swing.Swing.*
import scala.swing.event.*
import java.awt.image.BufferedImage
import java.awt.{Color, Graphics2D, Point, geom, Component, Dimension}
import javax.imageio.ImageIO
import javax.swing.Timer
import java.awt.event.ActionListener
import org.jocl.*
import org.jocl.CL.*
import javax.swing.border.BevelBorder

class Recognizer(video_feed: VideoFeed, database: Database) {
  private val alpha: Array[Char] = "abcdefghijklmnopqrstuvwxyz-' ".toCharArray()
  val tesseract = new Tesseract()
  tesseract.setDatapath("tessdata")
  var output_recognize: String = ""
  var output_database: String = ""

  def recognize(): Unit = {
    video_feed { image =>
      val text = tesseract.doOCR(image)
      val clean = text
        .toLowerCase()
        .toCharArray()
        .filter { char =>
          alpha.contains(char)
        }
        .mkString
      output_recognize = clean
      output_database = database.find(clean)
    }
  }
}

class RecognizerPanel(recognizer: Recognizer)
    extends BoxPanel(Orientation.Vertical) {
  border = TitledBorder(EtchedBorder(Raised), "Recognition Engine")
  val button_recognize = new Button("Recognize") {
    reactions += { case ButtonClicked(_) =>
      recognizer.recognize()
    }
  }

  val label_output_recognize = new Label {
    background = new java.awt.Color(0x33, 0x33, 0x33)
    preferredSize = new Dimension(250, 100)
    new Timer(
      1000 / 2,
      Swing.ActionListener { _ =>
        this.text = f"Recognition = ${recognizer.output_recognize}"
      }
    ).start()
  }

  val label_output_database = new Label {
    background = new java.awt.Color(0x33, 0x33, 0x33)
    preferredSize = new Dimension(250, 100)
    new Timer(
      1000 / 2,
      Swing.ActionListener { _ =>
        this.text = f"Closest Match = ${recognizer.output_database}"
      }
    ).start()
  }

  contents ++= Seq(
    button_recognize,
    label_output_recognize,
    label_output_database
  )
}
