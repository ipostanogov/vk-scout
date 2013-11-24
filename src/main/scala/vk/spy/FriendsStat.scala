package vk.spy

import scala.swing._
import scala.swing.event.ButtonClicked
import javax.swing.{JFileChooser, UIManager}
import BorderPanel.Position._
import org.apache.poi.xssf.usermodel.{XSSFSheet, XSSFWorkbook}
import java.io.{FileOutputStream, File}
import scala.util.Random
import vk.scout.wrap.users.UserFromVk
import vk.scout.wrap.friends.FriendsGet

object FriendsStat extends SimpleSwingApplication {
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)

  def top = new MainFrame {
    title = "Загрузчик друзей"
    contents = ui
    size = new Dimension(300, 80)
    peer.setLocationRelativeTo(null)
  }

  lazy val ui = new BorderPanel {
    val btSubmit = new Button("Скачать друзей")
    val idField = new TextField()
    var table = new Table()
    listenTo(btSubmit)
    reactions += {
      case ButtonClicked(`btSubmit`) =>
        makeUsersFriends(idField.text.split(",").map(UserFromVk.fromId))
    }
    layout(btSubmit) = South
    layout(idField) = Center
    layout(new Label(" id Пользователя ")) = West
  }

  def fillFriendsSheet(sheet: XSSFSheet, data: Map[UserFromVk, Int]) {
    val row = sheet.createRow(0)
    for ((header, num) <- Seq("Количество повторений", "Фамилия", "Имя", "id").zipWithIndex)
      row.createCell(num).setCellValue(header)
    for (((friend, reps), num) <- data.toList.sortBy(-_._2).zipWithIndex) {
      val row = sheet.createRow(num + 1)
      row.createCell(0).setCellValue(reps)
      row.createCell(1).setCellValue(friend.lastName)
      row.createCell(2).setCellValue(friend.firstName)
      row.createCell(3).setCellValue(friend.id)
    }
  }

  def makeUsersFriends(users: Seq[UserFromVk]) {
    val workbook = new XSSFWorkbook()
    val stat2 = users.map(user => (user, FriendsGet.getAll(user, depth = 2)))
    val sheet2 = workbook.createSheet("Все второго")
    fillFriendsSheet(sheet2, FriendsGet.merge(stat2.map(_._2)))
    val stat = users.map(user => (user, FriendsGet.getAll(user, depth = 1)))
    val sheet = workbook.createSheet("Все первого")
    fillFriendsSheet(sheet, FriendsGet.merge(stat.map(_._2)))
    stat.foreach(x => {
      val sheet = workbook.createSheet(x._1.firstName + " " + x._1.lastName + " (" + x._1.id + ")")
      fillFriendsSheet(sheet,x._2)
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
