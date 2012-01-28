package com.appfog.grails



import org.junit.*
import grails.test.mixin.*

@TestFor(PetsController)
@Mock(Pets)
class PetsControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/pets/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.petsInstanceList.size() == 0
        assert model.petsInstanceTotal == 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.petsInstance != null
    }

    void testSave() {
        controller.save()

        assert model.petsInstance != null
        assert view == '/pets/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/pets/show/1'
        assert controller.flash.message != null
        assert Pets.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/pets/list'


        populateValidParams(params)
        def pets = new Pets(params)

        assert pets.save() != null

        params.id = pets.id

        def model = controller.show()

        assert model.petsInstance == pets
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/pets/list'


        populateValidParams(params)
        def pets = new Pets(params)

        assert pets.save() != null

        params.id = pets.id

        def model = controller.edit()

        assert model.petsInstance == pets
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/pets/list'

        response.reset()


        populateValidParams(params)
        def pets = new Pets(params)

        assert pets.save() != null

        // test invalid parameters in update
        params.id = pets.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/pets/edit"
        assert model.petsInstance != null

        pets.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/pets/show/$pets.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        pets.clearErrors()

        populateValidParams(params)
        params.id = pets.id
        params.version = -1
        controller.update()

        assert view == "/pets/edit"
        assert model.petsInstance != null
        assert model.petsInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/pets/list'

        response.reset()

        populateValidParams(params)
        def pets = new Pets(params)

        assert pets.save() != null
        assert Pets.count() == 1

        params.id = pets.id

        controller.delete()

        assert Pets.count() == 0
        assert Pets.get(pets.id) == null
        assert response.redirectedUrl == '/pets/list'
    }
}
