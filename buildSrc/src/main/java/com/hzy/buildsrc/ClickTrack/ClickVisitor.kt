package com.hzy.buildsrc.ClickTrack

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.ASM7


/**
 * User: hzy
 * Date: 2020/7/19
 * Time: 8:05 PM
 * Description: ASM 遍历类
 */

class ClickVisitor(private val visitor: ClassVisitor) : ClassVisitor(ASM7, visitor) {
    private var className: String? = null
    override fun visit(version: Int, access: Int, name: String?, signature: String?, superName: String?, interfaces: Array<out String>?) {
        super.visit(version, access, name, signature, superName, interfaces)
        className = name
    }

    override fun visitMethod(access: Int, name: String?, descriptor: String?, signature: String?, exceptions: Array<out String>?): MethodVisitor {
        val visitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        return ClickMethodVisitor(visitor, access, name, descriptor, className)

    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        return super.visitAnnotation(descriptor, visible)
    }

}

