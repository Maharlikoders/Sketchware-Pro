package mod.elfilibustero.sketch.beans

class DependencyBean {
	companion object {
		fun from(string: String): DependencyBean {
			requireNotNull(string) { "null string value" }

			val strings = string.split(":")
			require(strings.size >= 3) { "Invalid dependency format, must be groupId:artifactId:version" }

			val dependency = Dependency()
			dependency.groupId = strings[0]
			dependency.artifactId = strings[1]
			dependency.version = strings[2]

			return dependency
		}
	}

	var groupId: String? = null
	var artifactId: String? = null
	var version: String? = null

	override fun toString(): String {
		return "$groupId:$artifactId:$version"
	}

	override fun hashCode(): Int {
		 return "$groupId:$artifactId".hashCode()
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || javaClass != other.javaClass) return false

		val that = other as Dependency

		if (groupId != that.groupId) return false
		return artifactId == that.artifactId
	}
}