package example

import org.json4s._
import org.json4s.native.JsonMethods._

import java.io.{File, PrintWriter}
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

//  val inp_file = "africa.json"
//  val out_file = "africa10.json"
//
//  def jsonFile = Source.fromFile(inp_file)

  var json_out: String = ""
  for (line <- jsonFile.getLines) json_out += line
  val parsedJson = parse(json_out)
    .extract[List[Country]]
    .filter(x=>x.region=="Africa")
    .sortBy(x=>x.area).reverse
    .take(10)
//  println(parsedJson.mkString("\n"))

//  json_out = "["
//  for (x <- parsedJson) {
//    val obj = JObject(List(
//      "name" -> JString(x.name.official),
//      "capital" -> JString(x.capital(0)),
//      "area" -> JLong(x.area)
//    ))
//    json_out += "\n"+ compact(render(obj)) + ","
//  }
//  json_out = json_out.stripSuffix(",").trim + "\n"+"]"
//  val outJson = pretty(render(Extraction.decompose(parse(json_out))))
//  println(outJson)

  val json_arr = for (x <- parsedJson) yield {
      JObject(List(
      "name" -> JString(x.name.official),
      "capital" -> JString(x.capital(0)),
      "area" -> JLong(x.area)
      ))
  }
//  val jArray = JArray(array.toList)

  val outJson = pretty(render(JArray(json_arr)))
//  println(outJson)

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

