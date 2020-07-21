package com.hzy.buildsrc.AsmUtil

import jdk.internal.org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * User: hzy
 * Date: 2020/7/22
 * Time: 12:34 AM
 * Description: 代码插桩工具
 */
object AsmLog {

}

fun AsmLog.e(visitor: MethodVisitor, tag: String, content: String) {
    visitor.visitLdcInsn(tag)
    visitor.visitLdcInsn(content)
    visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "e", "(Ljava/lang/String;Ljava/lang/String;)I", false)
    visitor.visitInsn(Opcodes.POP)
}
