package ${entityPackage};
<#-- Entity Import Statements -->
<#if (entityImportStatements)?? && (entityImportStatements?size > 0) >

<#list entityImportStatements as statement>
${statement};
</#list>
</#if>

/**
 *
 * This class was generated by EntityGenerator.
 * This class corresponds to the database table ${tableName}
 *
 * @date ${entityFileCreateDate}
 */
public class ${entityName}<#if (entitySerialVersionUID)??> implements Serializable</#if> {
	<#-- Entity SerialVersionUID -->
	<#if (entitySerialVersionUID)??>
	
	private static final long serialVersionUID = ${entitySerialVersionUID};
	</#if>

	<#-- Entity Property Statements -->
	<#list entityPropertyStatements as statement>
	${statement};
	</#list>
	<#-- Entity Associated Entity Statements -->
	<#if (entityAssociatedEntityStatements)?? && (entityAssociatedEntityStatements?size > 0) >
	
	<#list entityAssociatedEntityStatements as statement>
	${statement};
	</#list>
	</#if>
	<#-- Entity Constructor Statements -->
	<#if (entityConstructorStatements)?? && (entityConstructorStatements?size > 0) >
	
	<#list entityConstructorStatements as statement>
	${statement}
	</#list>
	</#if>
	
	<#list entityGetAndSetMethodStatements as statement>
	${statement}
	</#list>
}