package ru.arhipova.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

@UtilityClass
@SuppressWarnings("java:S1172")
public class LogAgent {
    private final String STRING_FIELD_DESCRIPTOR = "(Ljava/lang/String;)V";
    private final String STRING_BUILDER_NAME = "java/lang/StringBuilder";
    private final String STRING_BUILDER_APPEND_STRING_DESCRIPTOR = "(Ljava/lang/String;)Ljava/lang/StringBuilder;";
    private final String STRING_BUILDER_APPEND_OBJECT_DESCRIPTOR = "(Ljava/lang/Object;)Ljava/lang/StringBuilder;";
    private final String APPEND_OPERATION = "append";
    private final String INIT_OPERATION = "<init>";

    public void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new ClassFileTransformer() {
            @SneakyThrows
            @Override
            public byte[] transform(
                    ClassLoader loader,
                    String className,
                    Class<?> classBeingRedefined,
                    ProtectionDomain protectionDomain,
                    byte[] classfileBuffer) {
                ClassReader cr = new ClassReader(classfileBuffer);
                ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
                ClassVisitor cv = new ClassVisitor(Opcodes.ASM7, cw) {
                    @Override
                    public MethodVisitor visitMethod(
                            int access, String name, String descriptor, String signature, String[] exceptions) {
                        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                        return new LogMethodVisitor(api, mv, access, name, descriptor);
                    }
                };
                cr.accept(cv, Opcodes.ASM7);

                return cw.toByteArray();
            }
        });
    }

    private class LogMethodVisitor extends AdviceAdapter {
        private boolean logAnnotated;
        private final String methodName;

        protected LogMethodVisitor(int api, MethodVisitor mv, int access, String name, String desc) {
            super(api, mv, access, name, desc);
            this.methodName = name;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            if (descriptor.equals("Lru/arhipova/annotations/Log;")) {
                logAnnotated = true;
            }
            return super.visitAnnotation(descriptor, visible);
        }

        @Override
        protected void onMethodEnter() {
            if (!logAnnotated) {
                return;
            }

            Type[] argTypes = Type.getArgumentTypes(methodDesc);
            int argIndex = 1;

            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitTypeInsn(NEW, STRING_BUILDER_NAME);
            mv.visitInsn(DUP);
            mv.visitLdcInsn("executed method: " + methodName + "(");
            mv.visitMethodInsn(INVOKESPECIAL, STRING_BUILDER_NAME, INIT_OPERATION, STRING_FIELD_DESCRIPTOR, false);

            for (int i = 0; i < argTypes.length; i++) {
                Type argType = argTypes[i];

                mv.visitVarInsn(argType.getOpcode(ILOAD), argIndex);
                box(argType);
                mv.visitMethodInsn(
                        INVOKEVIRTUAL,
                        STRING_BUILDER_NAME,
                        APPEND_OPERATION,
                        STRING_BUILDER_APPEND_OBJECT_DESCRIPTOR,
                        false);

                if (i < argTypes.length - 1) {
                    addParamToStringBuilder(", ");
                }
                argIndex++;
            }
            addParamToStringBuilder(")");
            mv.visitMethodInsn(INVOKEVIRTUAL, STRING_BUILDER_NAME, "toString", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", STRING_FIELD_DESCRIPTOR, false);
        }

        private void addParamToStringBuilder(String param) {
            mv.visitLdcInsn(param);
            mv.visitMethodInsn(
                    INVOKEVIRTUAL,
                    STRING_BUILDER_NAME,
                    APPEND_OPERATION,
                    STRING_BUILDER_APPEND_STRING_DESCRIPTOR,
                    false);
        }
    }
}
