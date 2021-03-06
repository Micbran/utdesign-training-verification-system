import { Component, Input, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { NgbActiveModal } from "@ng-bootstrap/ng-bootstrap";
import { finalize } from "rxjs/operators";
import { trainingLevelStrings } from "src/app/enums";
import { iStudent } from "src/app/interfaces";
import { StudentService } from "src/app/services/student.service";
import { Helpers } from "src/helpers";
import { NotificationService } from 'src/app/services/notification.service'

@Component({
  selector: 'app-add-edit-student-modal',
  templateUrl: './add-edit-student-modal.component.html',
  styleUrls: ['./add-edit-student-modal.component.scss']
})

export class AddEditStudentModalComponent implements OnInit {
  @Input() student!: iStudent;
  @Input() editMode!: boolean;
  public formStudent!: FormGroup;
  public selectedTrainingLevel: string = "";
  public trainingLevelStringsExtra = trainingLevelStrings;

  constructor(
    private studentService: StudentService,
    private formBuilder: FormBuilder,
    public activeModal: NgbActiveModal,
    private notifyService : NotificationService
  ) {

  }

  ngOnInit() {
    if (!this.editMode) {
      this.student = {
        studentId: "",
        netId: "",
        firstName: "",
        lastName: "",
        trainingLevel: 0,
        identifier: ""
      }
    }
    this.selectedTrainingLevel = trainingLevelStrings[this.student.trainingLevel];

    this.formStudent = this.formBuilder.group({
      studentId: [this.student.studentId, [Validators.required, Helpers.validateStringIsNotEmpty()]],
      studentNetId: [this.student.netId, [Validators.required, Helpers.validateStringIsNotEmpty()]],
      studentFirstName: [this.student.firstName, [Validators.required, Helpers.validateStringIsNotEmpty()]],
      studentLastName: [this.student.lastName, [Validators.required, Helpers.validateStringIsNotEmpty()]],
      studentIdentifier: [this.student.identifier, []],
      studentTraining: [this.student.trainingLevel, []],
    });
  }

  selectTrainingLevel(newTrainingLevel: string) {
    this.selectedTrainingLevel = newTrainingLevel;
  }

  onClickSubmitButton(target: EventTarget | null) {
    this.editMode ? this.updateStudent(target)
      : this.createStudent(target);
  }

  updateStudent(target: EventTarget | null) {
    let updatedStudent = this.buildStudent();
    updatedStudent.studentPk = this.student.studentPk;

    let button = <HTMLButtonElement>target;
    button.disabled = true;
    this.studentService.updateStudent(updatedStudent).pipe(
      finalize(() => {
        button.disabled = false;
      }))
      .subscribe(
        (result: boolean) => {
          if (result) {
            Helpers.individualKeyCopy(updatedStudent, this.student);
            this.activeModal.close();
            this.notifyService.showSuccess("Student edited successfully!");
            return;
          }
        },
        error => {
          console.error("Unable to update student!: ", error);
          this.notifyService.showError(`Unable to update student!: ${error}`, "ERROR");
        }
      );
  }

  createStudent(target: EventTarget | null) {
    let newStudent = this.buildStudent();

    let button = <HTMLButtonElement>target;
    button.disabled = true;
    this.studentService.addStudent(newStudent).pipe(
      finalize(() => {
        button.disabled = false;
      }))
      .subscribe(
        (result: number) => {
          if (result) {
            newStudent.studentPk = result;
            this.activeModal.close({object: newStudent});
            this.notifyService.showSuccess("Student created successfully!");
            return;
          }
        },
        error => {
          console.error("Unable to create student!: ", error);
          this.notifyService.showError(`Unable to create student!: ${error}`, "ERROR");
        }
      );
  }

  buildStudent(): iStudent {
    let student: iStudent = {
      studentId: this.formStudent.get("studentId")!.value.trim(),
      netId: this.formStudent.get("studentNetId")!.value.trim(),
      firstName: this.formStudent.get("studentFirstName")!.value.trim(),
      lastName: this.formStudent.get("studentLastName")!.value.trim(),
      identifier: this.formStudent.get("studentIdentifier")!.value,
      trainingLevel: trainingLevelStrings.findIndex(tl => tl === this.selectedTrainingLevel)
    };
    return student;
  }


}
