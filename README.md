# LearnAC

Java implementation of an arithmetic circuits learner. This algorithm is similar to a Bayesian network learner with context-specific independence by greedily splitting conditional distributions, at each step scoring the candidates by compiling the resulting network into an arithmetic circuit, and using its size as the penalty.

The algorithm is based on "Learning Arithmetic Circuits" paper by Daniel Lowd and Pedro, Domingos Department of Computer Science and Engineering, University of Washington, Seattle, USA.



Input file template:

One line indicating the number of lines, followed by a line indicating the schema of the data. Lastly, followed by lines of data.

Schema is a line indicating domains of each variable. If a variable is ranging from 0 to v the schema line would have (v+1) for that variable.

------------------------------------------------------------------------------------------  
------------------------------------------------------------------------------------------  

Main classes to run:


1) QueryData. Used for inference, QueryData accepts one argument and that is the path to the data file. It is used to learn an AC based on input data, then query the data and compare the probabilities suggested by the AC versus counts of the data.

2) RunLearnAC. This java file accepts two arguments, the first one is the path to the data file. The second one is the number of data variables that are wanted to be analyzed. 

Example: java RunLearnAC <File Path> x

It would learn an AC based on the file in <File Path> and analyze only the first x variables. RunLearnAC outputs the JPD obtained by applying every possible configuration to the AC.

3) DrawAC. This java file accepts only one argument and that is the file path. It is set by default to only analyze 3 first variables, since the graphical representation can easily get messy in larger ACs.

To change this configuration, please change the arguments in “learn.readData(input,3,10);”

4) Learner. This java file goes through the steps of learning an AC step by step an provides verbose console outputs.


------------------------------------------------------------------------------------------  
------------------------------------------------------------------------------------------  

Note on graphical representation: If the AC is out of bounds of the panel, please change the ArithmeticCircuitDrawinJPanel.rootPoint and ArithmeticCircuitDrawinJPanel.rootLevel and set them to a value fitting the AC.