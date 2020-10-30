package co.edu.eafit.dis.st1607.carpetaciudadana.main
import co.edu.eafit.dis.st1607.carpetaciudadana.server.WebServer
import sun.misc.Signal

object Main {

  def main(args: Array[String]): Unit = {
    val server = WebServer()

    Signal.handle(new Signal("HUP"), (_: Signal) => {
      server.restart
    })

    Signal.handle(new Signal("INT"), (_: Signal) => {
      server.shutdown
    })

    Signal.handle(new Signal("TERM"), (_: Signal) => {
      server.shutdown
    })
  }
}
