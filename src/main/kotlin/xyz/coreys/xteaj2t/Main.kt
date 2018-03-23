package xyz.coreys.xteaj2t

fun main(args: Array<String>) {
	
	if (args.size < 2) {
		throw IllegalArgumentException("Input and output arguments are required")
	} else if (args.size > 2) {
		throw IllegalArgumentException("Illegal arguments specified")
	}
	
	val rawInput = args[0]
	val rawOutput = args[1]
	
	val (source, mode) = when {
		rawInput.endsWith(".json", true) -> {
			Pair(Source.File, Mode.JsonToTxt)
		}
		rawInput.startsWith("http", true) -> {
			Pair(Source.URL, Mode.JsonToTxt)
		}
		else -> {
			Pair(Source.Directory, Mode.TxtToJson)
		}
	}
	
	Conversion(source, mode, rawInput, rawOutput).convert()
}