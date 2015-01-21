C_SRCS = src/JnetFS.c 
OBJS = build/JnetFS.o 

LIBJVM_DIR := /usr/lib/jvm/java-7-openjdk/jre/lib/amd64/server/
INCJVM_DIR := ${JVM_DIR}include/
INCJVM_OS_DIR := ${INCJVM_DIR}linux/

LD_FLAGS := -ljvm -lfuse -L${LIBJVM_DIR}

CFLAGS := -g -O3 -Wall
CFLAGS += -I${INCJVM_DIR} -I${INCJVM_OS_DIR}
CFLAGS += -fmessage-length=0 -D_FILE_OFFSET_BITS=64

build/%.o: src/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	mkdir -p build
	gcc -fPIC ${CFLAGS} -c -o"$@" "$<" 
	@echo 'Finished building: $<'

# All Target
all: lib/libJnetFS.so

# Tool invocations
lib/libJnetFS.so: $(OBJS)
	@echo 'Building target: $@'
	@echo 'Invoking: GCC C Linker'
	gcc -shared -o"lib/libJnetFS.so" $(OBJS) $(LD_FLAGS) 
	@echo 'Finished building target: $@'
	@echo ' '

# Other Targets
clean:
	-rm -rf build/$(OBJS) lib/libJnetFS.so
	-@echo ' '

.PHONY: all clean 
