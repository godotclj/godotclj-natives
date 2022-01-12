ARCH=linux_64
JAVA_PATH=$(JAVA_HOME)/bin:$(PATH)

include common.mk

all: target/godotclj-natives.jar

src/c/jvm.cpp: src/c/jvm.h
src/c/callback.c: src/c/callback.h
build/obj/wrapper.o: build/gen/src/c/wrapper.c
build/obj/jvm.o: LDFLAGS += -lpthread
target/classes/godotclj/loader.class: NAMESPACE=godotclj.loader
build/lib/natives/$(ARCH)/libgodotclj_gdnative.so: LDFLAGS += -Wl,--no-as-needed -Wl,-rpath='$$ORIGIN' -rdynamic -shared -ljava -ljvm
build/lib/natives/$(ARCH)/libgodotclj_gdnative.so: build/obj/jvm.o build/obj/gdnative.o build/obj/callback.o build/obj/wrapper.o build/obj/godot_bindings.o

.PHONY: clean
clean:
	rm -fr build target
	make -C test-resources/natives-test clean

build/gen/src/c/wrapper.h build/gen/src/c/wrapper.c: build/gen/godot_bindings.txt build/gen/godot_bindings.json
	mkdir -p $(shell dirname $@)
	PATH=$(JAVA_PATH) \
	clojure -A:gen -M -e "(require 'godotclj.ffi.generator) (godotclj.ffi.generator/generate-wrapper \"$(shell dirname $@)\")"

target/classes/godotclj/%.class: src/clojure/godotclj/%.clj
	mkdir -p $(shell dirname $@)
	PATH=$(JAVA_PATH) \
	clojure -M -e "(with-bindings {#'*compile-path* \"target/classes\"} (compile '$(NAMESPACE)))"

build/godot-headers/api.json: godot-headers/api.json
	mkdir -p $(shell dirname $@)
	cp $< $@

LAYOUTS=
LAYOUTS += build/gen/godot_bindings.txt build/gen/godot_bindings.json
LAYOUTS += build/gen/wrapper.txt build/gen/wrapper.json
LAYOUTS += build/gen/callback.txt build/gen/callback.json

target/godotclj-natives.jar: build/lib/natives/$(ARCH)/libgodotclj_gdnative.so build/godot-headers/api.json target/classes/godotclj/loader.class $(LAYOUTS)
	mkdir -p $(shell dirname $@)
	PATH=$(JAVA_PATH) \
	clojure -T:build jar
