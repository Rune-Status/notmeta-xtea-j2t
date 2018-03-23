package xyz.coreys.xteaj2t

import com.google.gson.*
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

class Conversion(
	private val source: Source,
	private val mode: Mode,
	private val input: String,
	private val output: String
) {
	
	fun convert() {
		when (mode) {
			Mode.JsonToTxt -> toTxt()
			Mode.TxtToJson -> toJson()
		}
	}
	
	private fun toJson() {
	
	}
	
	private fun toTxt() {
		val startTime = System.currentTimeMillis()
		val outputDirectory = when {
			!Files.isDirectory(Paths.get(output)) -> throw IllegalArgumentException("Invalid output directory")
			else -> Paths.get(output)
		}
		
		val jsonArray = when (source) {
			Source.URL -> {
				val conn = URL(input).openConnection()
				conn.setRequestProperty("User-Agent", "Mozilla/5.0")
				
				JsonParser().parse(conn.getInputStream().reader()).asJsonArray
			}
			Source.File -> JsonParser().parse(Files.newBufferedReader(Paths.get(input))).asJsonArray
			else -> throw IllegalStateException("Invalid JSON input")
		} ?: throw JsonIOException("Invalid JSON Input")
		
		jsonArray.toList().parallelStream().forEach {
			val fileName = (it as JsonObject).get("region").asInt.toString()
			val keys = it.get("keys").asJsonArray.map { it.asInt }.joinToString(System.lineSeparator())
			val file = Paths.get(outputDirectory.toString(), "$fileName.txt")
			Files.write(file, keys.toByteArray(), StandardOpenOption.CREATE)
		}
		
		println("Parsed ${jsonArray.distinct().size} unique region key-sets to $outputDirectory in ${System.currentTimeMillis() - startTime}ms")
	}
	
}

enum class Source {
	URL,
	File,
	Directory
}

enum class Mode {
	JsonToTxt,
	TxtToJson
}