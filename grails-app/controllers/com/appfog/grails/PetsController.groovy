package com.appfog.grails

import org.springframework.dao.DataIntegrityViolationException

class PetsController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [petsInstanceList: Pets.list(params), petsInstanceTotal: Pets.count()]
    }

    def create() {
        [petsInstance: new Pets(params)]
    }

    def save() {
        def petsInstance = new Pets(params)
        if (!petsInstance.save(flush: true)) {
            render(view: "create", model: [petsInstance: petsInstance])
            return
        }

		flash.message = message(code: 'default.created.message', args: [message(code: 'pets.label', default: 'Pets'), petsInstance.id])
        redirect(action: "show", id: petsInstance.id)
    }

    def show() {
        def petsInstance = Pets.get(params.id)
        if (!petsInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'pets.label', default: 'Pets'), params.id])
            redirect(action: "list")
            return
        }

        [petsInstance: petsInstance]
    }

    def edit() {
        def petsInstance = Pets.get(params.id)
        if (!petsInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'pets.label', default: 'Pets'), params.id])
            redirect(action: "list")
            return
        }

        [petsInstance: petsInstance]
    }

    def update() {
        def petsInstance = Pets.get(params.id)
        if (!petsInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'pets.label', default: 'Pets'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (petsInstance.version > version) {
                petsInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'pets.label', default: 'Pets')] as Object[],
                          "Another user has updated this Pets while you were editing")
                render(view: "edit", model: [petsInstance: petsInstance])
                return
            }
        }

        petsInstance.properties = params

        if (!petsInstance.save(flush: true)) {
            render(view: "edit", model: [petsInstance: petsInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'pets.label', default: 'Pets'), petsInstance.id])
        redirect(action: "show", id: petsInstance.id)
    }

    def delete() {
        def petsInstance = Pets.get(params.id)
        if (!petsInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'pets.label', default: 'Pets'), params.id])
            redirect(action: "list")
            return
        }

        try {
            petsInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'pets.label', default: 'Pets'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'pets.label', default: 'Pets'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
