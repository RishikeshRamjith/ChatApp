## Makefile for Assignment 1
## Zainab Adjiet
## 28-03-2018

JAVAC=javac

.SUFFIXES: .java .class

SRCDIR=src
BINDIR=bin
DOCDIR=doc

$(BINDIR)/%.class: $(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) $<
	
CLASSES=Client.class ServerThread.class Server.class

CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/%.class)

default: $(CLASS_FILES)

runserver:
	java $(BINDIR)/Server

runclient:
	java $(BINDIR)/Client

javadocs:
	javadoc -d $(DOCDIR) $(SRCDIR)/*.java

clean:
	rm $(BINDIR)%/*.class

cleandocs:
	rm -r $(DOCDIR)/*
