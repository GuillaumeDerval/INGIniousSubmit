Submit to INGInious
===================

A plugin for Jetbrains' IDEs to submit programming exercises to [INGInious](https://github.com/UCL-INGI/INGInious).

Currently, it only supports Java/Python. It can be installed on any Jetbrains IDE that supports at least one of these
languages (even via plugins).

Configuration file
------------------

The plugin, when installed, is invisible by default.
It needs a configuration file, which must be located at the root of the project, 
and that is named `config.inginious`.

Here is an example of such a config file:
```
https://path-to-your-inginious-instance.com
yourCourseId
yourTaskId
firstSubproblemId java method true ClassName methodName
secondSubproblemId java method false ClassName methodName
anotherSubproblemId java class true AnotherClassName
```

In short, the first three lines indicates the location of the task to which the plugin will submit.
The remaining lines describes actions to be done on this submission, and, in this case, things to extract from the project
and put inside the INGInious submission.

This config file:

- Will upload the submission to `https://path-to-your-inginious-instance.com`, 
to the task with id `yourTaskId` inside the course with id `yourCourseId`.
- The submission will be composed of multiple answers to each subproblem inside the task:

  - Subproblem with id `firstSubproblemId` will receive as answer the body and the signature of the method `ClassName.methodName`.
    The `true` indicates that the signature should be included.
  - Subproblem with id `secondSubproblemId` will receive as answer the body of the method `ClassName.methodName`.
    The `false` indicates that the signature should not be included.
  - Subproblem with id `secondSubproblemId` will receive as answer the body and the signature of the class `AnotherClassName`.
    The `true` indicates that the signature should be included.

Actions
-------

Below are documented all the actions available inside the config file 
(i.e. all the things you can put below the first three lines of the configuration).

`SPID` always means Subproblem ID. Things in lower case are keywords. 
Unless noted otherwise, `LANG` is always either `java` or `python`. 

- `SPID LANG method EXTRACT_SIGNATURE CLASS_NAME METHOD_NAME` extracts the method `METHOD_NAME` from the class `CLASS_NAME`,
  and send it to INGInious as the answer for subproblem `SPID`. 
  `EXTRACT_SIGNATURE` can be either `true` or `false` and indicates if the signature of the method 
  must be sent to INGInious or not.
- `SPID LANG class EXTRACT_SIGNATURE CLASS_NAME` extracts the class `CLASS_NAME`,
  and send it to INGInious as the answer for subproblem `SPID`. 
  `EXTRACT_SIGNATURE` can be either `true` or `false` and indicates if the signature of the class 
  must be sent to INGInious or not.
  
Note that Python requires Fully Qualified Names for classes (for example, a class `C` inside a file 
`test.py` inside the package `this.is.a.package` has `this.is.a.package.test.C` as FQN).

TODO
----

- Add new code extractors for C/C++/Scala, which are used by UCLouvain for multiple courses.
- Add a code extractor that sends JAR file
- Add a code extractor that sends a ZIP file of the whole project
- Add a code extractor that allows to send specific files
- Modify the submission mechanism to allow to send binary files
- Add a command that ensures that tests have been run and are successful before sending a submission to INGInious
