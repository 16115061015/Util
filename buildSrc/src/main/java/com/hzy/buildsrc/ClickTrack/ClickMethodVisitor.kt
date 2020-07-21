package com.hzy.buildsrc.ClickTrack

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

/**
 * User: hzy
 * Date: 2020/7/19
 * Time: 8:42 PM
 * Description: 在方法前后插入代码
 */
private val TAG = "ClickAnnotationAdapter:  "

class ClickMethodVisitor(cv: MethodVisitor?, access: Int = 0, name: String?, desc: String?, private val className: String? = null) : AdviceAdapter(ASM7, cv, access, name, desc) {

    private var intercept = false
    override fun onMethodEnter() {
        super.onMethodEnter()
        if (!intercept) return
        //插入方法
        mv?.let {
            //插桩显示Toast  该方法必须有context和text参数
            val l0 = Label()
            mv.visitLabel(l0)
            mv.visitLineNumber(16, l0)
            mv.visitVarInsn(Opcodes.ALOAD, 1)
            mv.visitVarInsn(Opcodes.ALOAD, 2)
            mv.visitInsn(Opcodes.ICONST_0)
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/widget/Toast", "makeText", "(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;", false)
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "android/widget/Toast", "show", "()V", false)
            val l1 = Label()
            mv.visitLabel(l1)
            mv.visitLineNumber(17, l1)
            mv.visitInsn(Opcodes.RETURN)
            val l2 = Label()
            mv.visitLabel(l2)
            mv.visitLocalVariable("this", "Lcom/hzy/cnn/CustomView/Utils/ASMCode;", null, l0, l2, 0)
            mv.visitLocalVariable("context", "Landroid/content/Context;", null, l0, l2, 1)
            mv.visitLocalVariable("text", "Ljava/lang/String;", null, l0, l2, 2)
            mv.visitMaxs(3, 3)
        }
//        AsmLog.e(mv, "Click", "method:$name")
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        //访问注解判断是否要拦截方法 则不再往下走流程
        intercept = descriptor?.contains("Lcom/hzy/aopdemo/Track/ClickTrack") == true
        println(TAG + "visitAnnotation:${descriptor} --> $intercept")
        return super.visitAnnotation(descriptor, visible)
    }

    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
    }

}