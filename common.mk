JAVA_HOME=$(shell clojure -M -e "(println (System/getProperty \"java.home\"))")

JAVAC=$(JAVA_HOME)/bin/javac

INCLUDES=-Igodot-headers -Ibuild -Isrc/c -Ibuild/gen/src/c -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux
CPPFLAGS = -fPIC $(INCLUDES)
CFLAGS = --std=c11
CXXFLAGS =
LDFLAGS=-Lbuild/lib/natives/$(ARCH) -L $(JAVA_HOME)/lib -L $(JAVA_HOME)/lib/server -L /usr/lib64 -rdynamic

build/obj/%.o: src/c/%.c
	mkdir -p $(shell dirname $@)
	$(CC) $(CPPFLAGS) $(CFLAGS) -c $< -o $@

build/obj/%.o: build/gen/src/c/%.c
	mkdir -p $(shell dirname $@)
	$(CC) $(CPPFLAGS) $(CFLAGS) -c $< -o $@

build/obj/%.o: src/c/%.cpp
	mkdir -p $(shell dirname $@)
	$(CXX) $(CPPFLAGS) $(CXXFLAGS) -c $< -o $@

build/lib/natives/$(ARCH)/%.so:
	mkdir -p $(shell dirname $@)
	$(CC) $(CPPFLAGS) $(CFLAGS) $(LDFLAGS) -shared $^ -o $@

CLANGFLAGS=-D RUNTIME_GENERATION=1 $(CPPFLAGS) $(CFLAGS) -c $< -o $(shell mktemp).o -Xclang

build/gen/%.txt: src/c/%.c
	mkdir -p $(shell dirname $@)
	clang $(CLANGFLAGS) -fdump-record-layouts | sed $$'s/\e\\[[0-9;:]*[a-zA-Z]//g' > $@

build/gen/%.json: src/c/%.c
	mkdir -p $(shell dirname $@)
	clang $(CLANGFLAGS) -ast-dump=json > $@

build/gen/%.txt: build/gen/src/c/%.c
	mkdir -p $(shell dirname $@)
	clang $(CLANGFLAGS) -fdump-record-layouts | sed $$'s/\e\\[[0-9;:]*[a-zA-Z]//g' > $@

build/gen/%.json: build/gen/src/c/%.c
	mkdir -p $(shell dirname $@)
	clang $(CLANGFLAGS) -ast-dump=json > $@
