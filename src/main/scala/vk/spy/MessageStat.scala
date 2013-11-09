package vk.spy

import scala.swing._
import scala.swing.event.ButtonClicked
import javax.swing.{JFileChooser, UIManager}
import BorderPanel.Position._
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.{FileOutputStream, File}
import scala.util.Random
import vk.scout.wrap.users.UserFromVk

object MessageStat extends SimpleSwingApplication {
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)

  def top = new MainFrame {
    title = "Загрузчик сообщений"
    contents = ui
    size = new Dimension(300, 80)
    peer.setLocationRelativeTo(null)
  }

  lazy val ui = new BorderPanel {
    val btSubmit = new Button("Скачать сообщения")
    val idField = new TextField()
    var table = new Table()
    listenTo(btSubmit)
    reactions += {
      case ButtonClicked(`btSubmit`) =>
        showPostsOnUserWall(idField.text.split(",").map(UserFromVk.fromId))
    }
    layout(btSubmit) = South
    layout(idField) = Center
    layout(new Label(" id Пользователя ")) = West
  }

  def showPostsOnUserWall(users: Seq[UserFromVk]) {
    val workbook = new XSSFWorkbook()
    users.foreach(user => {
      val sheet = workbook.createSheet(user.firstName + " " + user.lastName + " (" + user.id + ")")
      val row = sheet.createRow(0)
      for ((header, num) <- Seq("Имя", "Фамилия", "id Автора", "Дата", "Текст").zipWithIndex)
        row.createCell(num).setCellValue(header)
      for ((post, num) <- user.postsOnWall.zipWithIndex) {
        val row = sheet.createRow(num + 1)
        row.createCell(0).setCellValue(post.author.firstName)
        row.createCell(1).setCellValue(post.author.lastName)
        row.createCell(2).setCellValue(post.authorId)
        row.createCell(3).setCellValue(post.normDate)
        row.createCell(4).setCellValue(post.text)
      }
    })
    try {
      val fc = new JFileChooser()
      fc.setSelectedFile(new File(System.getenv("user.home") + File.separator + Random.nextDouble + ".xlsx"))
      if (fc.showSaveDialog(this.top.peer) == JFileChooser.APPROVE_OPTION) {
        val out = new FileOutputStream(fc.getSelectedFile)
        workbook.write(out)
        out.close()
      }
    }
    catch {
      case e: Exception => e.printStackTrace()
    }
    System.exit(0)
  }
}
