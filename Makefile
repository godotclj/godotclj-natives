JAVA_PATH=$(JAVA_HOME)/bin:$(PATH)

CACHE=
CACHE += build/cache/$(ARCH)/function-bindings.json
CACHE += build/cache/$(ARCH)/structs.json
CACHE += build/cache/$(ARCH)/enums.json

LAYOUTS=
LAYOUTS += build/gen/godot_bindings.txt build/gen/godot_bindings.json
LAYOUTS += build/gen/wrapper.txt build/gen/wrapper.json
LAYOUTS += build/gen/callback.txt build/gen/callback.json

DEPS=build/lib/natives/$(ARCH)/libgodotclj_gdnative.so build/godot-headers/api.json classes/godotclj/ffi/loader.class $(LAYOUTS) $(CACHE)

all: $(DEPS)

include common.mk

src/c/jvm.cpp: src/c/jvm.h
src/c/callback.c: src/c/callback.h
build/obj/wrapper.o: build/gen/src/c/wrapper.c
build/obj/jvm.o: LDFLAGS += -lpthread
classes/godotclj/ffi/loader.class: NAMESPACE=godotclj.ffi.loader
build/lib/natives/$(ARCH)/libgodotclj_gdnative.so: LDFLAGS += -Wl,--no-as-needed -Wl,-rpath='$$ORIGIN' -rdynamic -shared -ljava -ljvm
build/lib/natives/$(ARCH)/libgodotclj_gdnative.so: build/obj/jvm.o build/obj/gdnative.o build/obj/callback.o build/obj/wrapper.o build/obj/godot_bindings.o

.PHONY: clean
clean:
	rm -fr build target classes
	make -C test-resources/natives-test clean

build/gen/src/c/wrapper.h build/gen/src/c/wrapper.c: build/gen/godot_bindings.txt build/gen/godot_bindings.json
	mkdir -p $(shell dirname $@)
	PATH=$(JAVA_PATH) \
	clojure -A:gen -M -e "(require 'godotclj.ffi.generator) (godotclj.ffi.generator/generate-wrapper \"$(shell dirname $@)\")"

build/cache/$(ARCH)/%.json:
	mkdir -p $(shell dirname $@)
	PATH=$(JAVA_PATH) \
	clojure -A:gen -M -e "(require 'godotclj.ffi.generator) (godotclj.ffi.generator/generate-cache \"$@\")"

build/cache: $(CACHE)

classes/godotclj/%.class: src/clojure/godotclj/%.clj
	mkdir -p $(shell dirname $@)
	PATH=$(JAVA_PATH) \
	clojure -M -e "(compile '$(NAMESPACE))"

build/godot-headers/api.json: godot-headers/api.json
	mkdir -p $(shell dirname $@)
	cp $< $@

target/godotclj-natives.jar: $(DEPS)
	mkdir -p $(shell dirname $@)
	PATH=$(JAVA_PATH) \
	clojure -T:build jar

all: $(DEPS)
