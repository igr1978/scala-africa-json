package example

import org.json4s._
import org.json4s.native.JsonMethods._

import java.io._
import scala.io.Source

object Main extends  App {

  if (args.length != 1) {
    println("Incorrect arguments.")
    println("Usage: /path/to/jar {path/to/output_json_file.json}")
    sys.exit(-1)
  }

  case class Name(common: String, official: String)
  case class Country(region: String, name: Name, capital: List[String], area: Long)

  implicit val formats = DefaultFormats

  val inp_file = "https://raw.githubusercontent.com/mledoze/countries/master/countries.json"
  val out_file = args(0)

  def jsonFile = Source.fromURL(inp_file)

  var json_out: String = ""
  for (line <- jsonFile.getLines) json_out += line
  val parsedJson = parse(json_out)
    .extract[List[Country]]
    .filter(x=>x.region=="Africa")
    .sortBy(x=>x.area).reverse
    .take(10)

  json_out = "["
  for (x <- parsedJson) {
    val obj = JObject(List(
      "name" -> JString(x.name.official.mkString),
      "capital" -> JString(x.capital.toList(0).mkString),
      "area" -> JLong(x.area)
    ))
    json_out += "\n"+ compact(render(obj)) + ","
  }
  json_out = json_out.stripSuffix(",").trim + "\n"+"]"
  val outJson = pretty(render(Extraction.decompose(parse(json_out))))

  if (args.length == 1) {
    val writer = new PrintWriter(new File(out_file))
    writer.write(outJson)
    writer.close()
  }

  val fi = new File(out_file)
  if (fi.exists()) {
    println("\nprint output file\n")
    println(Source.fromFile(out_file).getLines.mkString("\n"))
    println("\nfinish")
  }
  else
    println("\noutput file " + out_file + " not found\n")
}

