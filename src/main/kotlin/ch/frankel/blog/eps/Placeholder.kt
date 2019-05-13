package ch.frankel.blog.eps

/* For compiling purpose. */
fun run(filename: String) = WordFrequencyController().apply {
    dispatch(WordFrequencyController.InitMessage(filename))
}.dispatch(WordFrequencyController.RunMessage())