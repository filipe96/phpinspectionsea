package com.kalessil.phpStorm.phpInspectionsEA.inspectors.phpUnit.strategy;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import com.jetbrains.php.lang.psi.elements.BinaryExpression;
import com.jetbrains.php.lang.psi.elements.FunctionReference;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.kalessil.phpStorm.phpInspectionsEA.utils.OpenapiTypesUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AssertRegexStrategy {
    private final static Map<String, String> numberCompareTargets = new HashMap<>();
    private final static Set<String> binaryTargets = new HashSet<>();
    static {
        binaryTargets.add("assertTrue");
        binaryTargets.add("assertFalse");

        numberCompareTargets.put("assertSame",      "1");
        numberCompareTargets.put("assertNotSame",   "0");
        numberCompareTargets.put("assertEquals",    "1");
        numberCompareTargets.put("assertNotEquals", "0");
    }

    static public boolean apply(@NotNull String methodName, @NotNull MethodReference reference, @NotNull ProblemsHolder holder) {
        boolean result = false;
        if (numberCompareTargets.containsKey(methodName)) {
            final PsiElement[] arguments = reference.getParameters();
            if (arguments.length >= 2 && OpenapiTypesUtil.isNumber(arguments[0])) {
                final boolean isTarget = OpenapiTypesUtil.isFunctionReference(arguments[1]);
                if (isTarget) {
                    final FunctionReference candidate = (FunctionReference) arguments[1];
                    final String candidateName        = candidate.getName();
                    if (candidateName != null && candidateName.equals("preg_match")) {
                        final PsiElement[] innerArguments = candidate.getParameters();
                        if (innerArguments.length == 2) {
                            final boolean isDirect = arguments[0].getText().equals(numberCompareTargets.get(methodName));
                            holder.registerProblem(
                                    reference,
                                    (isDirect ? "assertRegExp" : "assertNotRegExp") + " should be used instead"
                            );
                            result = true;
                        }
                    }
                }
            }
        } else if (binaryTargets.contains(methodName)) {
            final PsiElement[] arguments = reference.getParameters();
            if (arguments.length > 0 && arguments[0] instanceof BinaryExpression) {
                final BinaryExpression binary = (BinaryExpression) arguments[0];
                if (binary.getOperationType() == PhpTokenTypes.opGREATER) {
                    final PsiElement left  = binary.getLeftOperand();
                    final PsiElement right = binary.getRightOperand();
                    if (OpenapiTypesUtil.isNumber(right) && OpenapiTypesUtil.isFunctionReference(left)) {
                        final boolean isTargetNumber = right.getText().equals("0");
                        if (isTargetNumber) {
                            final FunctionReference candidate = (FunctionReference) left;
                            final String candidateName        = candidate.getName();
                            if (candidateName != null && candidateName.equals("preg_match")) {
                                final PsiElement[] innerArguments = candidate.getParameters();
                                if (innerArguments.length == 2) {
                                    final boolean isDirect = methodName.equals("assertTrue");
                                    holder.registerProblem(
                                            reference,
                                            (isDirect ? "assertRegExp" : "assertNotRegExp") + " should be used instead"
                                    );
                                    result = true;

                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
}