package com.rudra.ispnetworktool.presentation.nat_rule_templates

data class NatRuleTemplatesState(
    val templates: List<NatRuleTemplate> = emptyList(),
    val selectedTemplate: NatRuleTemplate? = null
)

data class NatRuleTemplate(
    val name: String,
    val description: String,
    val platform: String,
    val command: String
)
