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

C_DEPS += build/JnetFS.d 


LIBJVM_DIR=/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.3.x86_64/jre/lib/amd64/server

# Each subdirectory must supply rules for building sources it contributes
build/%.o: src/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	gcc -g -O3 -Wall -c -fmessage-length=0 -D_FILE_OFFSET_BITS=64 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -o"$@" "$<" -fPIC
	@echo 'Finished building: $<'
	@echo ' 
	
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
