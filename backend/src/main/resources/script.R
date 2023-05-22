install.packages("lintr")
install.packages("goodpractice")
install.packages("cyclocomp")

library(lintr)
library(goodpractice)
library(cyclocomp)

setwd("<path>")
analysis_file <- "<filename>"
results <- lint(file = analysis_file)

for (i in 1:length(results)) {
  cat("File:", results$filename[i], "\n")
  cat("Line:", results$line[i], "\n")
  cat("Column:", results$column[i], "\n")
  cat("Message:", results$message[i], "\n\n")
}

cyclocomp::cyclocomp(analysis_file)

q()
