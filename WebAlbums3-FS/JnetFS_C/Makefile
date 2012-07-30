################################################################################
# Automatically-generated file. Do not edit!
################################################################################

RM := rm -rf

# All of the sources participating in the build are defined here
################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += src/JnetFS.c 

OBJS += build/JnetFS.o 

#C_DEPS += build/JnetFS.d 


LIBJVM_DIR=/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.3.x86_64/jre/lib/amd64/server
INCJVM_DIR=/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.3.x86_64/include/
INCJVM_OS_DIR=/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.3.x86_64/include/linux/

# Each subdirectory must supply rules for building sources it contributes
build/%.o: src/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	mkdir -p lib build
	gcc -g -O3 -Wall -c -fmessage-length=0 -D_FILE_OFFSET_BITS=64 -o"$@" "$<" -fPIC -I${INCJVM_DIR} -I${INCJVM_OS_DIR}
	@echo 'Finished building: $<'
	
LIBS := -ljvm -lfuse -L${LIBJVM_DIR}

ifneq ($(MAKECMDGOALS),clean)
ifneq ($(strip $(C_DEPS)),)
-include $(C_DEPS)
endif
endif

-include ../makefile.defs

# Add inputs and outputs from these tool invocations to the build variables 

# All Target
all: lib/libJnetFS.so

# Tool invocations
lib/libJnetFS.so: $(OBJS)
	@echo 'Building target: $@'
	@echo 'Invoking: GCC C Linker'
	gcc -shared -o"lib/libJnetFS.so" $(OBJS) $(LIBS) 
	@echo 'Finished building target: $@'
	@echo ' '

# Other Targets
clean:
	-$(RM) build/$(OBJS) $(C_DEPS) $(LIBRARIES) lib/libJnetFS.so
	-@echo ' '

.PHONY: all clean dependents
.SECONDARY:

-include ../makefile.targets
