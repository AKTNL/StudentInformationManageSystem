import { createRouter, createWebHistory } from "vue-router";
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'
import { isLoggedIn, hasRole, ROLES } from '../utils/auth'

const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/login',
        name: 'Login',
        component: Login,
        meta: { requiresAuth: false }
    },
    {
        path: '/home',
        name: 'Home',
        component: Home,
        meta: { requiresAuth: true }
    },
    {
        path: '/admin/students',
        name: 'StudentList',
        component: () => import('../views/admin/StudentList.vue'),
        meta: { requiresAuth: true, roles: [ROLES.ADMIN] }
    },
    {
        path: '/admin/courses',
        name: 'CourseList',
        component: () => import('../views/admin/CourseList.vue'),
        meta: { requiresAuth: true, roles: [ROLES.ADMIN] }
    },
    {
        path: '/student/courses',
        name: 'CourseSelect',
        component: () => import('../views/student/CourseSelect.vue'),
        meta: { requiresAuth: true, roles: [ROLES.STUDENT] }
    },
    {
        path: '/student/schedule',
        name: 'CourseSchedule',
        component: () => import('../views/student/CourseSchedule.vue'),
        meta: { requiresAuth: true, roles: [ROLES.STUDENT] }
    },
    {
        path: '/student/profile',
        name: 'MyProfile',
        component: () => import('../views/student/MyProfile.vue'),
        meta: { requiresAuth: true, roles: [ROLES.STUDENT] }
    },
    {
        path: '/:pathMatch(.*)*',
        redirect: '/login'
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
    const requiredRoles = to.meta.roles

    if (!requiresAuth) {
        if (to.path === '/login' && isLoggedIn()) {
            next('/home')
            return
        }
        next()
        return
    }

    if (!isLoggedIn()) {
        next({
            path: '/login',
            query: { redirect: to.fullPath }
        })
        return
    }

    if (requiredRoles && !hasRole(requiredRoles)) {
        next('/home')
        return
    }

    next()
})

export default router
