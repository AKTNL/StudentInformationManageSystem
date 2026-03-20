export const ROLES = {
  ADMIN: 1,
  STUDENT: 2,
  TEACHER: 3
}

const USER_KEY = 'user_info'

export function getUser() {
  const userStr = localStorage.getItem(USER_KEY)
  if (userStr) {
    try {
      return JSON.parse(userStr)
    } catch (e) {
      return null
    }
  }
  return null
}

export function setUser(user) {
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

export function removeUser() {
  localStorage.removeItem(USER_KEY)
}

export function isLoggedIn() {
  return getUser() !== null
}

export function hasRole(role) {
  const user = getUser()
  if (!user) return false
  if (Array.isArray(role)) {
    return role.includes(user.role)
  }
  return user.role === role
}

export function isAdmin() {
  return hasRole(ROLES.ADMIN)
}

export function isStudent() {
  return hasRole(ROLES.STUDENT)
}

export default {
  ROLES,
  getUser,
  setUser,
  removeUser,
  isLoggedIn,
  hasRole,
  isAdmin,
  isStudent
}
